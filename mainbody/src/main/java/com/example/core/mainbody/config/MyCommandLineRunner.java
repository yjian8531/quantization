package com.example.core.mainbody.config;

import com.example.core.common.entity.FinanceDetail;
import com.example.core.common.entity.FinancialWallet;
import com.example.core.common.entity.WalletIncome;
import com.example.core.common.mapper.FinanceDetailMapper;
import com.example.core.common.mapper.FinancialWalletMapper;
import com.example.core.common.mapper.UserFinanceMapper;
import com.example.core.common.mapper.WalletIncomeMapper;
import com.example.core.common.utils.RedisUtil;
import com.example.core.common.utils.StringUtils;
import com.example.core.mainbody.job.BSCUSDTMonitor;
import com.example.core.mainbody.job.ETHUSDTMonitor;
import com.example.core.mainbody.job.TRXUSDTMonitor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private FinanceDetailMapper financeDetailMapper;


    @Autowired
    private FinancialWalletMapper financialWalletMapper;

//    @Autowired
//    private FinancialBalanceMapper financialBalanceMapper;


    @Autowired
    private UserFinanceMapper userFinanceMapper;

    @Autowired
    private WalletIncomeMapper walletIncomeMapper;

    @Override
    public void run(String... args) throws Exception {

        List<FinancialWallet> list = financialWalletMapper.selectAll();

        List<String> bscList = new ArrayList<>();
        List<String> ethList = new ArrayList<>();
        List<String> trxList = new ArrayList<>();

        for(FinancialWallet financialWallet : list){
            if("BEP20".equals(financialWallet.getType())){
                bscList.add(financialWallet.getAddress());
            }else if("ERC20".equals(financialWallet.getType())){
                ethList.add(financialWallet.getAddress());
            }else if("TRC20".equals(financialWallet.getType())){
                trxList.add(financialWallet.getAddress());
            }
        }
        Thread thEth = new Thread(){
            @Override
            public void run() {
                ethMonitor(ethList);
            }
        };
        Thread thBsc = new Thread(){
            @Override
            public void run() {
                bscMonitor(bscList);
            }
        };
        Thread thTrx = new Thread(){
            @Override
            public void run() {
                trxMonitor(trxList);
            }
        };
        thEth.start();
        thBsc.start();
        thTrx.start();







    }

    /**
     * 以太坊链充值监控
     * @param addressList
     */
    private void ethMonitor(List<String> addressList){
        try {
            // 创建监控器
            ETHUSDTMonitor monitor = new ETHUSDTMonitor();

            for(String address : addressList ){
                // 添加监控地址
                monitor.addMonitorAddress(address);
            }

            // 设置回调
            monitor.setDepositCallback(new ETHUSDTMonitor.DepositCallback() {
                @Override
                public void onDeposit(String from, String to, BigDecimal amount,
                                      String txHash, long blockNumber) {
                    FinancialWallet financialWallet = financialWalletMapper.selectByAddress(to.toLowerCase());
                    if(financialWallet != null){
                        // 这里可以实现你自己的业务逻辑
                        // 例如：保存到数据库、发送通知等
                        System.out.println("用户["+to+"]收到ERC20充值: " + amount + " USDT 来自 " + from);
                        userFinanceMapper.updateBalanceByUserId(financialWallet.getUserId(),"add",amount);
                        WalletIncome walletIncome = new WalletIncome();
                        walletIncome.setUserId(financialWallet.getUserId());
                        walletIncome.setType("ERC20");
//                        walletIncome.setBlock(BigDecimal.valueOf(blockNumber).intValue());
                        walletIncome.setBlock(blockNumber);
                        walletIncome.setHash(txHash);
                        walletIncome.setFromAddress(from);
                        walletIncome.setToAddress(to.toLowerCase());
                        walletIncome.setAmount(amount);
                        walletIncome.setStatus(0);
                        walletIncome.setCreateTime(new Date());
                        walletIncome.setUpdateTime(new Date());
                        walletIncomeMapper.insertSelective(walletIncome);

                        // 写入用户财务账单
                        FinanceDetail financeDetail = new FinanceDetail();
                        financeDetail.setUserId(financialWallet.getUserId());
                        financeDetail.setType(0); // 0=充值
                        financeDetail.setCoinType("USDT");
                        financeDetail.setDirection(0); // 0=收入
                        financeDetail.setTag("topup");
                        financeDetail.setMoneyNum(amount);
                        financeDetail.setWay(0);
                        financeDetail.setStatus(1); // 1=成功
                        financeDetail.setTxHash(txHash);
                        financeDetail.setChainType("ERC20");
                        financeDetail.setRemarks("USDT充值到账");
                        financeDetail.setCreateTime(new Date());
                        financeDetail.setUpdateTime(new Date());
                        financeDetailMapper.insertSelective(financeDetail);

                    }

                }
            });

            // 启动监控
            monitor.start();

            // 新地址扫描
            Timer addressTimer = new Timer(true);
            addressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    String addressCache = RedisUtil.get("NEW_ADDRESS_ERC20");
//                    if(StringUtils.isNotEmpty(addressCache)){
//                        JSONArray array = JSONArray.fromObject(addressCache);
//                        for(Object obj : array){
//                            // 添加监控地址
//                            monitor.addMonitorAddress(obj.toString());
//                        }
//                        RedisUtil.del("NEW_ADDRESS_ERC20");
//                    }
                    Set<String> newAddresses = RedisUtil.smembers("NEW_ADDRESS_ERC20");
                    if(newAddresses != null && !newAddresses.isEmpty()){
                        for(String address : newAddresses){
                            monitor.addMonitorAddress(address);
                        }
                        RedisUtil.del("NEW_ADDRESS_ERC20");
                    }
                }
            }, 2000, 1000); // 每秒刷新监控地址

            // 定期打印状态
            Timer statusTimer = new Timer(true);
            statusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<String, Object> status = monitor.getStatus();
                    System.out.println("\n=== ETH监控状态 ===");
                    System.out.println("运行状态: " + status.get("running"));
                    System.out.println("当前区块: " + status.get("currentBlock"));
                    System.out.println("最新区块: " + status.get("latestBlock"));
                    System.out.println("同步延迟: " + status.get("syncDelay") + " 区块");
                    System.out.println("监控地址: " + status.get("monitorAddresses") + " 个");
                    System.out.println("错误计数: " + status.get("errorCount"));
                    System.out.println("当前节点: " + status.get("currentNode"));
                    System.out.println("================\n");
                }
            }, 30000, 30000); // 每30秒打印一次


            // 等待程序退出
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 币安链充值监控
     * @param addressList
     */
    private void bscMonitor(List<String> addressList){
        try {
            // 创建监控器
            BSCUSDTMonitor monitor = new BSCUSDTMonitor();

            for(String address : addressList ){
                // 添加监控地址（替换为你的地址）
                monitor.addMonitorAddress(address);
            }

            // 设置回调
            monitor.setDepositCallback(new BSCUSDTMonitor.DepositCallback() {
                @Override
                public void onDeposit(String from, String to, BigDecimal amount,
                                      String txHash, long blockNumber) {

                    FinancialWallet financialWallet = financialWalletMapper.selectByAddress(to.toLowerCase());
                    if(financialWallet != null){
                        // 这里可以实现你自己的业务逻辑
                        // 例如：保存到数据库、发送通知等
                        System.out.println("用户["+to+"]收到BEP20充值: " + amount + " USDT 来自 " + from);
                        userFinanceMapper.updateBalanceByUserId(financialWallet.getUserId(),"add",amount);
                        WalletIncome walletIncome = new WalletIncome();
                        walletIncome.setUserId(financialWallet.getUserId());
                        walletIncome.setType("BEP20");
//                        walletIncome.setBlock(BigDecimal.valueOf(blockNumber).intValue());
                        walletIncome.setBlock(blockNumber);
                        walletIncome.setHash(txHash);
                        walletIncome.setFromAddress(from);
                        walletIncome.setToAddress(to.toLowerCase());
                        walletIncome.setAmount(amount);
                        walletIncome.setStatus(0);
                        walletIncome.setCreateTime(new Date());
                        walletIncome.setUpdateTime(new Date());
                        walletIncomeMapper.insertSelective(walletIncome);

                        // 写入用户财务账单
                        FinanceDetail financeDetail = new FinanceDetail();
                        financeDetail.setUserId(financialWallet.getUserId());
                        financeDetail.setType(0);
                        financeDetail.setCoinType("USDT");
                        financeDetail.setDirection(0);
                        financeDetail.setTag("topup");
                        financeDetail.setMoneyNum(amount);
                        financeDetail.setWay(0);
                        financeDetail.setStatus(1);
                        financeDetail.setTxHash(txHash);
                        financeDetail.setChainType("BEP20");
                        financeDetail.setRemarks("USDT充值到账");
                        financeDetail.setCreateTime(new Date());
                        financeDetail.setUpdateTime(new Date());
                        financeDetailMapper.insertSelective(financeDetail);

                    }


                }
            });

            // 启动监控
            monitor.start();

            // 新地址扫描
            Timer addressTimer = new Timer(true);
            addressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    String addressCache = RedisUtil.get("NEW_ADDRESS_BEP20");
//                    if(StringUtils.isNotEmpty(addressCache)){
//                        JSONArray array = JSONArray.fromObject(addressCache);
//                        for(Object obj : array){
//                            // 添加监控地址
//                            monitor.addMonitorAddress(obj.toString());
//                        }
//                        RedisUtil.del("NEW_ADDRESS_BEP20");
//                    }
                    Set<String> newAddresses = RedisUtil.smembers("NEW_ADDRESS_BEP20");
                    if(newAddresses != null && !newAddresses.isEmpty()){
                        for(String address : newAddresses){
                            monitor.addMonitorAddress(address);
                        }
                        RedisUtil.del("NEW_ADDRESS_BEP20");
                    }
                }
            }, 2000, 1000); // 每秒刷新监控地址

            // 定期打印状态
            Timer statusTimer = new Timer(true);
            statusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<String, Object> status = monitor.getStatus();
                    System.out.println("\n=== BNB监控状态 ===");
                    System.out.println("运行状态: " + status.get("running"));
                    System.out.println("当前区块: " + status.get("currentBlock"));
                    System.out.println("最新区块: " + status.get("latestBlock"));
                    System.out.println("同步延迟: " + status.get("syncDelay") + " 区块");
                    System.out.println("监控地址: " + status.get("monitorAddresses") + " 个");
                    System.out.println("错误计数: " + status.get("errorCount"));
                    System.out.println("当前节点: " + status.get("currentNode"));
                    System.out.println("================\n");
                }
            }, 30000, 30000); // 每30秒打印一次



            // 等待程序退出
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 波场链充值监控
     * @param addressList
     */
    private void trxMonitor(List<String> addressList){
        try {
            // 创建监控器
            TRXUSDTMonitor monitor = new TRXUSDTMonitor();

            for(String address : addressList ){
                // 添加监控地址
                monitor.addMonitorAddress(address);
            }

            // 设置回调
            monitor.setDepositCallback(new TRXUSDTMonitor.DepositCallback() {
                @Override
                public void onDeposit(String from, String to, BigDecimal amount,
                                      String txHash, long blockNumber) {
                    FinancialWallet financialWallet = financialWalletMapper.selectByAddress(to);
                    if(financialWallet != null){
                        log.info("用户[{}]收到TRC20充值: {} USDT 来自 {}", to, amount, from);

                        // 1. 更新用户余额
                        userFinanceMapper.updateBalanceByUserId(financialWallet.getUserId(), "add", amount);

                        // 2. 写入链上流水表
                        WalletIncome walletIncome = new WalletIncome();
                        walletIncome.setUserId(financialWallet.getUserId());
                        walletIncome.setType("TRC20");
                        walletIncome.setBlock(blockNumber);
                        walletIncome.setHash(txHash);
                        walletIncome.setFromAddress(from);
                        walletIncome.setToAddress(to);
                        walletIncome.setAmount(amount);
                        walletIncome.setStatus(0);
                        walletIncome.setCreateTime(new Date());
                        walletIncome.setUpdateTime(new Date());
                        walletIncomeMapper.insertSelective(walletIncome);

                        // 3. 写入用户财务账单
                        FinanceDetail financeDetail = new FinanceDetail();
                        financeDetail.setUserId(financialWallet.getUserId());
                        financeDetail.setType(0);
                        financeDetail.setCoinType("USDT");
                        financeDetail.setDirection(0);
                        financeDetail.setTag("topup");
                        financeDetail.setMoneyNum(amount);
                        financeDetail.setWay(0);
                        financeDetail.setStatus(1);
                        financeDetail.setTxHash(txHash);
                        financeDetail.setChainType("TRC20");
                        financeDetail.setRemarks("USDT充值到账");
                        financeDetail.setCreateTime(new Date());
                        financeDetail.setUpdateTime(new Date());
                        financeDetailMapper.insertSelective(financeDetail);
                    }
                }
            });

            // 启动监控
            monitor.start();

            // 新地址扫描
            Timer addressTimer = new Timer(true);
            addressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    String addressCache = RedisUtil.get("NEW_ADDRESS_TRC20");
//                    if(StringUtils.isNotEmpty(addressCache)){
//                        JSONArray array = JSONArray.fromObject(addressCache);
//                        for(Object obj : array){
//                            // 添加监控地址
//                            monitor.addMonitorAddress(obj.toString());
//                        }
//                        RedisUtil.del("NEW_ADDRESS_TRC20");
//                    }
                    Set<String> newAddresses = RedisUtil.smembers("NEW_ADDRESS_TRC20");
                    if(newAddresses != null && !newAddresses.isEmpty()){
                        for(String address : newAddresses){
                            monitor.addMonitorAddress(address);
                        }
                        RedisUtil.del("NEW_ADDRESS_TRC20");
                    }
                }
            }, 2000, 1000); // 每秒刷新监控地址

            // 定期打印状态
            Timer statusTimer = new Timer(true);
            statusTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<String, Object> status = monitor.getStatus();
                    System.out.println("\n=== TRX监控状态 ===");
                    System.out.println("运行状态: " + status.get("running"));
                    System.out.println("当前区块: " + status.get("currentBlock"));
                    System.out.println("最新区块: " + status.get("latestBlock"));
                    System.out.println("同步延迟: " + status.get("syncDelay") + " 区块");
                    System.out.println("监控地址: " + status.get("monitorAddresses") + " 个");
                    System.out.println("错误计数: " + status.get("errorCount"));
                    System.out.println("当前节点: " + status.get("currentNode"));
                    System.out.println("================\n");
                }
            }, 30000, 30000); // 每30秒打印一次

            // 等待程序退出
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
