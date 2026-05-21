package com.example.core.mainbody.controller;

import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单模块
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建策略订单
     * 请求示例：POST /message/create {"productId": 1, "apikeyId": 1, "symbol": "BTC/USDT", "nodeTime": "15", "paramStr": "{}"}
     */
    @PostMapping(value = "/create", produces = {"application/json"})
    public ResultMessage createStrategyOrder(@RequestBody CreateStrategyOrderSO so, @RequestParam String userId) {
        log.info("创建策略订单请求参数: so={}, userId={}", so, userId);
        return orderService.createStrategyOrder(so, userId);
    }

    /**
     * 启动策略
     * 请求示例：POST /message/start?orderNo=xxx
     */
    @PostMapping(value = "/start", produces = {"application/json"})
    public ResultMessage startStrategyOrder(@RequestParam String orderNo) {
        log.info("启动策略请求参数: orderNo={}", orderNo);
        return orderService.startStrategyOrder(orderNo);
    }

    /**
     * 停止策略
     * 请求示例：POST /message/stop?orderNo=xxx
     */
    @PostMapping(value = "/stop", produces = {"application/json"})
    public ResultMessage stopStrategyOrder(@RequestParam String orderNo) {
        log.info("停止策略请求参数: orderNo={}", orderNo);
        return orderService.stopStrategyOrder(orderNo);
    }

    /**
     * 接收交易日志推送
     * 请求示例：POST /message/trade {"strategyId": "xxx", "tradeNo": "xxx", ...}
     */
    @PostMapping(value = "/trade", produces = {"application/json"})
    public ResultMessage receiveTradeLog(@RequestBody TradeLogPushSO tradeLog) {
        log.info("接收交易日志推送: tradeLog={}", tradeLog);
        return orderService.receiveTradeLog(tradeLog);
    }

    /**
     * 接收仓位信息推送
     * 请求示例：POST /message/position {"strategyId": "xxx", "tradeBl": "buy", ...}
     */
    @PostMapping(value = "/position", produces = {"application/json"})
    public ResultMessage receivePositionInfo(@RequestBody PositionPushSO position) {
        log.info("接收仓位信息推送: position={}", position);
        return orderService.receivePositionInfo(position);
    }

}
