package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.StrategyInfo;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.ConfigRobotSO;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import com.example.core.mainbody.so.robot.QueryHistoryPositionSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
import com.example.core.mainbody.so.robot.QueryTradeRecordSO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单模块Controller
 * 负责机器人订单的创建、查询和管理功能
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

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

    /**
     * 查询用户可用交易所列表
     * 用于配置机器人时选择交易所
     * 返回用户已绑定的API Key对应的交易所信息
     */
    @PostMapping(value = "/exchange/list", produces = {"application/json"})
    public ResultMessage queryExchangeList() {
        String userId = getUserId();
        return orderService.queryExchangeList(userId);
    }

    /**
     * 查询可用币对列表
     * 用于配置机器人时选择交易币对
     * 返回系统支持的币对（如ETH/USDT、BTC/USDT等）
     */
    @PostMapping(value = "/symbol/list", produces = {"application/json"})
    public ResultMessage querySymbolList() {
        return orderService.querySymbolList();
    }



    /**
     * 查询用户机器人列表
     * 对应原型图：机器人列表页
     * 请求示例：POST /order/robot/list {"exchange": 0}  (0=币安, 1=Gate, null=全部)
     * 返回：订单列表（包含收益、运行时长、状态等）
     */
    @PostMapping(value = "/robot/list", produces = {"application/json"})
    public ResultMessage queryRobotList(@RequestBody QueryRobotSO so) {
        String userId = getUserId();
        return orderService.queryRobotList(userId, so);
    }

    /**
     * 查询机器人详情
     * 对应原型图：机器人详情页（基础信息+当前仓位）
     * 请求示例：POST /order/robot/detail?id=3241
     * 返回：订单基础信息、收益数据、当前持仓信息
     */
    @PostMapping(value = "/robot/detail", produces = {"application/json"})
    public ResultMessage queryRobotDetail(@RequestParam Integer id) {
        String userId = getUserId();
        return orderService.queryRobotDetail(userId, id);
    }

    /**
     * 查询历史仓位列表
     * 对应原型图：机器人详情页 - 历史仓位部分
     * 请求示例：POST /order/position/history {"orderId": 3241, "pageNum": 1, "pageSize": 10}
     * 返回：已平仓的历史仓位列表（分页）
     */
    @PostMapping(value = "/position/history", produces = {"application/json"})
    public ResultMessage queryHistoryPositionList(@RequestBody QueryHistoryPositionSO so) {
        String userId = getUserId();
        return orderService.queryHistoryPositionList(userId, so);
    }


    /**
     * 查询交易记录列表
     * 对应原型图：机器人详情页 - 交易记录部分
     * 请求示例：POST /order/trade/record {"orderId": 3241, "pageNum": 1, "pageSize": 10}
     * 返回：交易记录列表（分页，包含交易所、币对、买卖方向、价格、收益）
     */
    @PostMapping(value = "/trade/record", produces = {"application/json"})
    public ResultMessage queryTradeRecordList(@RequestBody QueryTradeRecordSO so) {
        String userId = getUserId();
        return orderService.queryTradeRecordList(userId, so);
    }

    /**
     * 接收策略状态心跳上报
     */
    @PostMapping(value = "/status", produces = {"application/json"})
    public ResultMessage receiveStrategyStatus(@RequestBody String statusJson) {
        log.info("接收策略状态上报: {}", statusJson);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "已接收");
    }

    // ==================== 策略模板 CRUD ====================

    /**
     * 新增策略模板
     */
    @PostMapping(value = "/strategy/add", produces = {"application/json"})
    public ResultMessage addStrategyInfo(@RequestBody StrategyInfo strategyInfo) {
        log.info("新增策略模板: {}", strategyInfo.getStrategyName());
        return orderService.addStrategyInfo(strategyInfo);
    }

    /**
     * 修改策略模板
     */
    @PostMapping(value = "/strategy/update", produces = {"application/json"})
    public ResultMessage updateStrategyInfo(@RequestBody StrategyInfo strategyInfo) {
        log.info("修改策略模板: id={}", strategyInfo.getId());
        return orderService.updateStrategyInfo(strategyInfo);
    }

    /**
     * 删除策略模板
     */
    @PostMapping(value = "/strategy/delete", produces = {"application/json"})
    public ResultMessage deleteStrategyInfo(@RequestParam Integer id) {
        log.info("删除策略模板: id={}", id);
        return orderService.deleteStrategyInfo(id);
    }

    /**
     * 查询策略模板详情
     */
    @GetMapping(value = "/strategy/detail", produces = {"application/json"})
    public ResultMessage queryStrategyInfo(@RequestParam String strategyId) {
        log.info("查询策略模板详情: strategyId={}", strategyId);
        return orderService.queryStrategyInfo(strategyId);
    }

    /**
     * 查询策略模板列表
     */
    @GetMapping(value = "/strategy/list", produces = {"application/json"})
    public ResultMessage queryStrategyInfoList() {
        log.info("查询策略模板列表");
        return orderService.queryStrategyInfoList();
    }


}
