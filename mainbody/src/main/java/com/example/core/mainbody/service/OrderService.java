package com.example.core.mainbody.service;

import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {

    /**
     * 创建策略订单
     */
    @Transactional
    ResultMessage createStrategyOrder(CreateStrategyOrderSO so, String userId);

    /**
     * 启动策略
     */
    @Transactional
    ResultMessage startStrategyOrder(String orderNo);

    /**
     * 停止策略
     * @param orderNo
     * @return
     */
    ResultMessage stopStrategyOrder(String orderNo);


    /**
     * 接收交易日志推送
     */
    @Transactional
    ResultMessage receiveTradeLog(TradeLogPushSO tradeLog);


    /**
     * 接收仓位信息推送
     */
    @Transactional
    ResultMessage receivePositionInfo(PositionPushSO position);

}
