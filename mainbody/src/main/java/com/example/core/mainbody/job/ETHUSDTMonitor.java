package com.example.core.mainbody.job;


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ETH链USDT充值监控程序
 * 基于BSC版本修改，适配ETH网络
 */
public class ETHUSDTMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ETHUSDTMonitor.class);

    // ETH网络节点（可以使用Infura、Alchemy等）
    private static final String[] ETH_NODES = {
            "https://eth.llamarpc.com",
            "https://eth-mainnet.g.alchemy.com/v2/YOUR_ALCHEMY_API_KEY",
            "https://rpc.ankr.com/eth",
            "https://cloudflare-eth.com",
            "https://ethereum.publicnode.com"
    };

    // ETH链上USDT合约地址（ERC-20）
    private static final String USDT_CONTRACT = "0xdAC17F958D2ee523a2206206994597C13D831ec7";

    // Transfer事件
    private static final Event TRANSFER_EVENT = new Event(
            "Transfer",
            Arrays.asList(
                    new TypeReference<Address>() {},
                    new TypeReference<Address>() {},
                    new TypeReference<Uint256>() {}
            )
    );

    // 连接配置
    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 30;

    // 监控状态
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final AtomicLong lastBlock = new AtomicLong(0);
    private final AtomicInteger nodeIndex = new AtomicInteger(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    // 线程池
    private ScheduledExecutorService scheduler;
    private ExecutorService workerPool;

    // Web3j客户端
    private Web3j web3j;

    // 监控地址列表
    private final Set<String> monitorAddresses = new CopyOnWriteArraySet<>();

    // 回调接口
    private DepositCallback depositCallback;

    public interface DepositCallback {
        void onDeposit(String from, String to, BigDecimal amount, String txHash, long blockNumber);
    }

    public ETHUSDTMonitor() {
        // 默认配置
    }

    public ETHUSDTMonitor(Collection<String> addresses) {
        if (addresses != null) {
            monitorAddresses.addAll(addresses);
        }
    }

    /**
     * 添加监控地址
     */
    public void addMonitorAddress(String address) {
        if (isValidAddress(address) && !monitorAddresses.contains(address.toLowerCase())) {
            monitorAddresses.add(address.toLowerCase());
            logger.info("添加ETH监控地址: {}", address);
        }
    }

    /**
     * 设置回调
     */
    public void setDepositCallback(DepositCallback callback) {
        this.depositCallback = callback;
    }

    /**
     * 启动监控
     */
    public void start() {
        if (running.get()) {
            logger.warn("监控已在运行中");
            return;
        }

        logger.info("启动ETH USDT充值监控...");
        logger.info("监控地址数量: {}", monitorAddresses.size());

        // 初始化线程池
        scheduler = Executors.newScheduledThreadPool(3);
        workerPool = Executors.newFixedThreadPool(5);

        // 初始化Web3j连接
        initWeb3jConnection();

        running.set(true);

        // 启动区块轮询（ETH出块较慢，可以延长轮询间隔）
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (running.get() && !shutdown.get()) {
                    pollNewBlocks();
                }
            }
        }, 0, 15, TimeUnit.SECONDS); // ETH每15秒轮询一次

        // 启动健康检查
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (running.get() && !shutdown.get()) {
                    healthCheck();
                }
            }
        }, 60, 60, TimeUnit.SECONDS);

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stop();
            }
        });

        logger.info("ETH USDT监控启动成功");
    }

    /**
     * 初始化Web3j连接
     */
    private void initWeb3jConnection() {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                String nodeUrl = getCurrentNodeUrl();
                logger.info("连接ETH节点: {}", nodeUrl);

                // 创建OkHttpClient
                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true);

                // 添加拦截器处理重试
                clientBuilder.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Response response = null;
                        IOException exception = null;

                        // 重试3次
                        for (int i = 0; i < 3; i++) {
                            try {
                                response = chain.proceed(request);
                                if (response.isSuccessful()) {
                                    return response;
                                } else {
                                    response.close();
                                    if (i == 2) {
                                        throw new IOException("HTTP " + response.code() + ": " + response.message());
                                    }
                                }
                            } catch (IOException e) {
                                exception = e;
                                if (i == 2) {
                                    throw exception;
                                }
                            }

                            // 等待后重试
                            try {
                                Thread.sleep(1000 * (i + 1));
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new IOException("重试被中断", e);
                            }
                        }

                        throw exception != null ? exception : new IOException("请求失败");
                    }
                });

                OkHttpClient httpClient = clientBuilder.build();
                HttpService httpService = new HttpService(nodeUrl, httpClient, false);

                web3j = Web3j.build(httpService);

                // 测试连接
                EthBlock.Block latestBlock = web3j.ethGetBlockByNumber(
                        DefaultBlockParameterName.LATEST, false
                ).send().getBlock();

                lastBlock.set(latestBlock.getNumber().longValue());
                logger.info("ETH连接成功，当前区块高度: {}", latestBlock.getNumber());

                return;

            } catch (Exception e) {
                retryCount++;
                logger.error("ETH连接失败，尝试重连 {}/{}: {}", retryCount, maxRetries, e.getMessage());

                // 切换到下一个节点
                switchToNextNode();

                // 等待后重试
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        throw new RuntimeException("无法连接到ETH节点，请检查网络连接或节点配置");
    }

    /**
     * 轮询新区块
     */
    private void pollNewBlocks() {
        try {
            if (web3j == null) {
                logger.error("Web3j连接未初始化");
                return;
            }

            // 获取最新区块
            BigInteger latestBlockNum = web3j.ethBlockNumber().send().getBlockNumber();
            long latestBlock = latestBlockNum.longValue();
            long lastBlockNum = lastBlock.get();

            if (latestBlock <= lastBlockNum) {
                // 没有新区块
                return;
            }

            logger.debug("ETH扫描新区块: {} -> {}", lastBlockNum, latestBlock);

            // ETH出块较慢，每次扫描最多50个区块
            long endBlock = Math.min(latestBlock, lastBlockNum + 50);

            // 扫描每个新区块
            for (long blockNum = lastBlockNum + 1; blockNum <= endBlock; blockNum++) {
                try {
                    scanBlock(blockNum);
                } catch (Exception e) {
                    logger.error("扫描ETH区块 {} 失败: {}", blockNum, e.getMessage());
                    errorCount.incrementAndGet();
                }
            }

            // 更新当前区块
            lastBlock.set(endBlock);

        } catch (Exception e) {
            logger.error("ETH轮询区块失败: {}", e.getMessage());
            errorCount.incrementAndGet();

            // 错误次数过多，尝试重新连接
            if (errorCount.get() > 10) {
                logger.warn("错误次数过多，尝试重新连接...");
                reconnect();
            }
        }
    }

    /**
     * 扫描单个区块
     */
    private void scanBlock(long blockNumber) throws Exception {
        // 创建过滤器
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)),
                DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)),
                USDT_CONTRACT
        );

        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));

        // 获取日志
        EthLog ethLog = web3j.ethGetLogs(filter).send();
        List<EthLog.LogResult> logs = ethLog.getLogs();

        if (logs == null || logs.isEmpty()) {
            return;
        }

        logger.debug("ETH区块 {} 发现 {} 条转账记录", blockNumber, logs.size());

        // 处理每个日志
        for (EthLog.LogResult logResult : logs) {
            try {
                Log log = (Log) logResult.get();
                processTransferLog(log);
            } catch (Exception e) {
                logger.error("处理ETH交易日志失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 处理转账日志
     */
    private void processTransferLog(Log log) {
        try {
            // 解析事件参数
            List<String> topics = log.getTopics();
            if (topics.size() < 3) {
                return;
            }

            // 解析地址（去除0x前缀后，取后40个字符）
            String from = parseAddress(topics.get(1));
            String to = parseAddress(topics.get(2));

            // 解析金额
            String data = log.getData();
            if (data == null || data.length() < 3) {
                return;
            }

            // 16进制转BigInteger
            BigInteger amountWei = new BigInteger(data.substring(2), 16);

            // USDT有6位小数（ETH USDT是6位，不是18位！）
            BigDecimal amount = new BigDecimal(amountWei).divide(BigDecimal.TEN.pow(6));

            // 检查是否监控地址
            if (isMonitoredAddress(to)) {
                // 提交到工作线程处理
                workerPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        handleDeposit(from, to, amount, log.getTransactionHash(),
                                log.getBlockNumber().longValue());
                    }
                });
            }

        } catch (Exception e) {
            logger.error("解析ETH转账日志失败: {}", e.getMessage());
        }
    }

    /**
     * 处理充值
     */
    private void handleDeposit(String from, String to, BigDecimal amount,
                               String txHash, long blockNumber) {
        try {
            // 获取确认数（ETH需要12个确认）
            int confirmations = getConfirmations(blockNumber);


            logger.info("💰 ETH USDT充值通知");
            logger.info("时间: {}", new Date());
            logger.info("交易哈希: {}", txHash);
            logger.info("区块高度: {}", blockNumber);
            logger.info("确认数: {}/12", confirmations);
            logger.info("转出地址: {}", from);
            logger.info("充值地址: {}", to);
            logger.info("充值金额: {} USDT", amount);
            logger.info("区块链链接: https://etherscan.io/tx/{}", txHash);


            // 调用回调
            if (depositCallback != null) {
                depositCallback.onDeposit(from, to, amount, txHash, blockNumber);
            }

            // 如果确认数足够（ETH通常需要12个确认）
            if (confirmations >= 12) {
                handleConfirmedDeposit(from, to, amount, txHash, blockNumber);
            }

        } catch (Exception e) {
            logger.error("处理ETH充值失败: {}", e.getMessage());
        }
    }

    /**
     * 处理已确认的充值
     */
    private void handleConfirmedDeposit(String from, String to, BigDecimal amount,
                                        String txHash, long blockNumber) {
        try {
            logger.info("ETH USDT充值已确认: {} USDT 到 {}", amount, to);

            // 这里可以添加业务逻辑：
            // 1. 保存到数据库
            // 2. 发送通知
            // 3. 更新用户余额
            saveToDatabase(from, to, amount, txHash, blockNumber);

        } catch (Exception e) {
            logger.error("处理已确认ETH充值失败: {}", e.getMessage());
        }
    }

    /**
     * 获取交易确认数
     */
    private int getConfirmations(long blockNumber) {
        try {
            if (web3j == null) {
                return 0;
            }

            BigInteger latestBlock = web3j.ethBlockNumber().send().getBlockNumber();
            return latestBlock.subtract(BigInteger.valueOf(blockNumber)).intValue();

        } catch (Exception e) {
            logger.error("获取ETH确认数失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 解析地址
     */
    private String parseAddress(String topic) {
        if (topic == null || topic.length() < 42) {
            return "";
        }

        // 地址格式：0x + 64个字符，最后40个是地址
        return "0x" + topic.substring(topic.length() - 40).toLowerCase();
    }

    /**
     * 检查是否监控地址
     */
    private boolean isMonitoredAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        // 检查地址格式
        if (!address.startsWith("0x") || address.length() != 42) {
            return false;
        }

        return monitorAddresses.contains(address.toLowerCase());
    }

    /**
     * 验证地址格式
     */
    private boolean isValidAddress(String address) {
        return address != null &&
                address.startsWith("0x") &&
                address.length() == 42 &&
                address.matches("0x[0-9a-fA-F]{40}");
    }

    /**
     * 健康检查
     */
    private void healthCheck() {
        try {
            if (web3j == null) {
                logger.error("ETH Web3j连接丢失，尝试重新连接");
                reconnect();
                return;
            }

            // 测试连接
            BigInteger blockNum = web3j.ethBlockNumber().send().getBlockNumber();
            long current = lastBlock.get();
            long delay = blockNum.longValue() - current;

            if (delay > 100) {
                logger.warn("ETH区块同步延迟: {} 个区块", delay);
            }

            // 重置错误计数
            if (errorCount.get() > 0) {
                errorCount.set(0);
            }

            logger.debug("ETH健康检查通过，当前区块: {}", blockNum);

        } catch (Exception e) {
            logger.error("ETH健康检查失败: {}", e.getMessage());
            errorCount.incrementAndGet();

            // 重新连接
            if (errorCount.get() > 5) {
                reconnect();
            }
        }
    }

    /**
     * 重新连接
     */
    private void reconnect() {
        logger.info("尝试重新连接ETH节点...");

        if (web3j != null) {
            try {
                web3j.shutdown();
            } catch (Exception e) {
                logger.error("关闭ETH Web3j连接失败: {}", e.getMessage());
            }
            web3j = null;
        }

        // 切换到下一个节点
        switchToNextNode();

        // 重新初始化连接
        try {
            initWeb3jConnection();
            logger.info("ETH重新连接成功");
        } catch (Exception e) {
            logger.error("ETH重新连接失败: {}", e.getMessage());
        }
    }

    /**
     * 切换到下一个节点
     */
    private void switchToNextNode() {
        int index = nodeIndex.incrementAndGet() % ETH_NODES.length;
        nodeIndex.set(index);
        logger.info("切换到ETH节点: {}", ETH_NODES[index]);
    }

    /**
     * 获取当前节点URL
     */
    private String getCurrentNodeUrl() {
        return ETH_NODES[nodeIndex.get() % ETH_NODES.length];
    }

    /**
     * 保存到数据库（示例方法）
     */
    private void saveToDatabase(String from, String to, BigDecimal amount,
                                String txHash, long blockNumber) {
        // 这里实现数据库保存逻辑
        // 例如：
        // String sql = "INSERT INTO eth_deposits (from_address, to_address, amount, tx_hash, block_number) VALUES (?, ?, ?, ?, ?)";
        // 使用JDBC或ORM框架执行
        logger.info("保存ETH充值记录到数据库: {} USDT 从 {} 到 {}", amount, from, to);
    }

    /**
     * 停止监控
     */
    public void stop() {
        if (!running.get() || shutdown.get()) {
            return;
        }

        logger.info("停止ETH USDT监控...");

        shutdown.set(true);
        running.set(false);

        // 关闭Web3j
        if (web3j != null) {
            try {
                web3j.shutdown();
                logger.info("ETH Web3j连接已关闭");
            } catch (Exception e) {
                logger.error("关闭ETH Web3j连接失败: {}", e.getMessage());
            }
        }

        // 关闭线程池
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (workerPool != null) {
            workerPool.shutdown();
            try {
                if (!workerPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    workerPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                workerPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        logger.info("ETH监控已停止");
    }

    /**
     * 获取监控状态
     */
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", running.get());
        status.put("currentBlock", lastBlock.get());
        status.put("monitorAddresses", monitorAddresses.size());
        status.put("errorCount", errorCount.get());
        status.put("currentNode", getCurrentNodeUrl());

        try {
            if (web3j != null) {
                BigInteger latest = web3j.ethBlockNumber().send().getBlockNumber();
                status.put("latestBlock", latest);
                status.put("syncDelay", latest.longValue() - lastBlock.get());
            }
        } catch (Exception e) {
            // 忽略
        }

        return status;
    }

    /**
     * 主方法 - 示例使用
     */
    public static void main(String[] args) {
        try {
            // 创建监控器
            ETHUSDTMonitor monitor = new ETHUSDTMonitor();

            // 添加监控地址（替换为你的地址）
            monitor.addMonitorAddress("0x742d35Cc6634C0532925a3b844Bc9e97D4e6034");
            monitor.addMonitorAddress("0x1111111111111111111111111111111111111111");

            // 设置回调
            monitor.setDepositCallback(new DepositCallback() {
                @Override
                public void onDeposit(String from, String to, BigDecimal amount,
                                      String txHash, long blockNumber) {
                    // 这里可以实现你自己的业务逻辑
                    System.out.println("收到ETH链USDT充值: " + amount + " USDT 来自 " + from);

                    // 例如：发送通知
                    sendNotification(from, to, amount, txHash);
                }
            });

            // 启动监控
            monitor.start();

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

            // 保持程序运行
            System.out.println("ETH USDT监控已启动，按Ctrl+C停止...");
            System.out.println("注意：请确保ETH_NODES配置了可用的节点URL");

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
     * 发送通知（示例方法）
     */
    private static void sendNotification(String from, String to, BigDecimal amount, String txHash) {
        // 这里可以实现发送通知的逻辑
        // 例如：调用Webhook、发送邮件、发送Telegram消息等
        System.out.println("发送通知: " + amount + " USDT 从 " + from + " 充值到 " + to);
    }
}
