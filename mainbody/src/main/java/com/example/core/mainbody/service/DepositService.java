package com.example.core.mainbody.service;

import com.example.core.common.utils.ResultMessage;
import java.math.BigDecimal;
/**
 * 充值服务接口
 */
public interface DepositService {

    /**
     * 获取用户指定网络的充值地址
     * 业务逻辑：
     * 1. 若该用户已绑定该网络地址，直接返回旧地址。
     * 2. 若未绑定，从地址池分配一个空闲地址给用户（带锁防并发），并返回。
     * 
     * @param userId 用户 ID
     * @param networkType 网络类型（如 BEP20, ERC20, TRC20）
     * @return 充值地址信息
     */
    ResultMessage getDepositAddress(String userId, String networkType);

    /**
     * 创建充值订单
     * 业务逻辑：
     * 1. 校验用户是否拥有该网络的充值地址。
     * 2. 生成唯一充值订单号，记录充值金额和状态（待确认）。
     * 3. 供后续区块链监听回调更新订单状态使用。
     * 
     * @param userId 用户 ID
     * @param networkType 网络类型
     * @param amount 用户计划充值的金额
     * @return 订单信息及收款地址
     */
    ResultMessage createDepositOrder(String userId, String networkType, BigDecimal amount);

    /**
     * 处理区块链充值回调（通常由定时任务或 Webhook 触发）
     * 业务逻辑：
     * 1. 根据 tx_hash 或地址金额匹配待确认订单。
     * 2. 更新订单状态为成功，并写入 w_finance_detail 财务明细表。
     * 3. 增加用户账户余额。
     * 
     * @param txHash 链上交易哈希
     * @param address 充值地址
     * @param amount 实际到账金额
     * @return 处理结果
     */
    ResultMessage handleDepositCallback(String txHash, String address, BigDecimal amount);

    /**
     * 查询用户充值订单列表
     * 业务逻辑：分页查询用户的充值记录，展示充值进度。
     * 
     * @param userId 用户 ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 订单列表
     */
    ResultMessage queryDepositOrderList(String userId, Integer pageNum, Integer pageSize);
}
