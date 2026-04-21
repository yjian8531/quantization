package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.DepositAddress;
import com.example.core.common.entity.DepositOrder;
import com.example.core.common.entity.FinanceDetail;
import com.example.core.common.mapper.DepositAddressMapper;
import com.example.core.common.mapper.DepositOrderMapper;
import com.example.core.common.mapper.FinanceDetailMapper;
import com.example.core.common.mapper.UserFinanceMapper;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.DepositService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class DepositServiceImpl implements DepositService {

    @Autowired
    private DepositAddressMapper depositAddressMapper;
    @Autowired
    private DepositOrderMapper depositOrderMapper;
    @Autowired
    private FinanceDetailMapper financeDetailMapper;
    @Autowired
    private UserFinanceMapper userFinanceMapper;

    /**
     * 获取用户指定网络的充值地址
     * 逻辑：若已绑定则返回旧地址；若未绑定则从地址池分配并锁定
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage getDepositAddress(String userId, String networkType) {
        // 1. 查是否已有地址
        DepositAddress existing = depositAddressMapper.selectByUserAndNetwork(userId, networkType);
        if (existing != null) {
            return buildAddressResult(existing.getAddress(), networkType);
        }

        // 2. 获取空闲地址（行锁防并发）
        DepositAddress unused = depositAddressMapper.selectUnusedAddress(networkType);
        if (unused == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "当前网络地址不足");
        }

        // 3. 绑定给用户
        int updated = depositAddressMapper.bindAddressToUser(unused.getAddress(), userId);
        if (updated == 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "地址分配失败");
        }

        return buildAddressResult(unused.getAddress(), networkType);
    }

    /**
     * 创建充值订单
     * 逻辑：确保用户有地址后，生成待确认状态的订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage createDepositOrder(String userId, String networkType, BigDecimal amount) {
        // 1. 确保用户有该网络地址
        DepositAddress addr = depositAddressMapper.selectByUserAndNetwork(userId, networkType);
        if (addr == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "请先获取充值地址");
        }

        // 2. 生成订单号（格式：D + 时间戳 + 6 位随机数）
        String orderNo = "D" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // 3. 插入订单表（状态 0=待确认）
        DepositOrder order = new DepositOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddress(addr.getAddress());
        order.setNetworkType(networkType);
        order.setAmount(amount);
        order.setStatus(0);
        order.setCreateTime(new Date());
        depositOrderMapper.insertSelective(order);

        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", orderNo);
        map.put("address", addr.getAddress());
        map.put("amount", amount);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "下单成功", map);
    }

    /**
     * 处理区块链充值回调（核心交互：订单表 → 财务表 → 用户余额）
     * 通常由后台定时任务扫描链上数据后调用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage handleDepositCallback(String txHash, String address, BigDecimal amount) {
        // 1. 根据地址和金额查找待确认订单
        DepositOrder order = depositOrderMapper.selectPendingByAddressAndAmount(address, amount);
        if (order == null) {
            log.warn("未找到匹配的待确认订单，地址:{}, 金额:{}", address, amount);
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }

        // 2. 检查是否已处理过（防重复回调）
        if (order.getStatus() == 1) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "订单已处理");
        }

        // 3. 更新订单状态为成功
        order.setStatus(1);
        order.setTxHash(txHash);
        order.setConfirmTime(new Date());
        depositOrderMapper.updateByPrimaryKeySelective(order);

        // 4. 写入财务明细表（w_finance_detail）
        FinanceDetail detail = new FinanceDetail();
        detail.setUserId(order.getUserId());
        detail.setOrderNo(order.getOrderNo());
        detail.setType(0); // 0=充值
        detail.setMoneyNum(amount);
        detail.setDirection(0); // 0=收入
        detail.setTag("topup");
        detail.setWay(0); // 0=账号余额
        detail.setStatus(1); // 1=完成
        detail.setTxHash(txHash);
        detail.setChainType(order.getNetworkType());
        detail.setRemarks("充值到账，订单号:" + order.getOrderNo());
        detail.setCreateTime(new Date());
        detail.setUpdateTime(new Date());
        financeDetailMapper.insertSelective(detail);

        // 5. 更新用户财务表余额（w_user_finance）
        // tad="add" 表示增加余额
        int updated = userFinanceMapper.updateBalanceByUserId(order.getUserId(), "add", amount);
        if (updated == 0) {
            log.warn("用户财务表不存在，需先初始化：userId={}", order.getUserId());
        }

        log.info("充值成功，用户:{}, 订单:{}, 金额:{}", order.getUserId(), order.getOrderNo(), amount);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "充值入账成功");
    }

    /**
     * 查询用户充值订单列表
     */
    @Override
    public ResultMessage queryDepositOrderList(String userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<DepositOrder> page = (Page<DepositOrder>) depositOrderMapper.selectByUserId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("list", page.getResult());
        map.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, map);
    }

    private ResultMessage buildAddressResult(String address, String networkType) {
        Map<String, Object> map = new HashMap<>();
        map.put("address", address);
        map.put("networkType", networkType);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "成功", map);
    }
}
