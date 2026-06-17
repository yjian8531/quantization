package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.ApikeyInfo;
import com.example.core.common.entity.StrategyInfo;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.order.CheckStrategyParamSO;
import com.example.core.mainbody.so.order.UpdateStrategyParamSO;
import com.example.core.mainbody.so.order.UpdateStrategySO;
import com.example.core.mainbody.so.order.UpdateStrategyTagSO;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import com.example.core.mainbody.so.robot.QueryHistoryPositionSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
import com.example.core.mainbody.so.robot.QueryTradeRecordSO;
import com.example.core.mainbody.so.robot.OrderNoSO;
import com.example.core.mainbody.so.robot.SetRobotPublicSO;
import com.example.core.mainbody.so.robot.IdSO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
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


    /**
     * 策略参数风控评估
     * @param checkStrategyParamSO
     * @return
     */
    @PostMapping(value = "/check/param", produces = {"application/json"})
    public ResultMessage checkStrategyParam(@RequestBody CheckStrategyParamSO checkStrategyParamSO){
        log.info("策略参数风控参数: ParamStr={}", checkStrategyParamSO.getParamStr());
        return orderService.checkStrategyParam(checkStrategyParamSO.getParamStr());
    }

    /**
     * 创建策略订单
     * 请求示例：POST /message/create {"productId": 1, "apikeyId": 1, "symbol": "BTC/USDT", "nodeTime": "15", "paramStr": "{}"}
     */
    @PostMapping(value = "/create", produces = {"application/json"})
    public ResultMessage createStrategyOrder(@RequestBody CreateStrategyOrderSO so) {
        UserInfo userInfo = this.getLoginUser();
        log.info("创建策略订单请求参数: so={}, userId={}", so, userInfo.getUserId());
        return orderService.createStrategyOrder(so, userInfo.getUserId());
    }

    /**
     * 启动策略
     * 请求示例：POST /order/start {"orderNo": "xxx"}
     */
    @PostMapping(value = "/start", produces = {"application/json"})
    public ResultMessage startStrategyOrder(@RequestBody OrderNoSO so) {
        log.info("启动策略请求参数: orderNo={}", so.getOrderNo());
        return orderService.startStrategyOrder(so.getOrderNo());
    }

    /**
     * 停止策略
     * 请求示例：POST /order/stop {"orderNo": "xxx"}
     */
    @PostMapping(value = "/stop", produces = {"application/json"})
    public ResultMessage stopStrategyOrder(@RequestBody OrderNoSO so) {
        log.info("停止策略请求参数: orderNo={}", so.getOrderNo());
        return orderService.stopStrategyOrder(so.getOrderNo());
    }

    /**
     * 重启策略
     * @param so
     * @return
     */
    @PostMapping(value = "/restart", produces = {"application/json"})
    public ResultMessage restartStrategyOrder(@RequestBody OrderNoSO so){
        log.info("重启策略请求参数: orderNo={}", so.getOrderNo());
        return orderService.restartStrategyOrder(so.getOrderNo());
    }


    /**
     * 更新策略平仓
     * @param updateStrategyTagSO
     * @return
     */
    @PostMapping(value = "/update/tag", produces = {"application/json"})
    public ResultMessage updateStrategyTag(@RequestBody UpdateStrategyTagSO updateStrategyTagSO){
        return orderService.updateStrategyTag(updateStrategyTagSO);
    }

    /**
     * 更换策略和币对
     * @param updateStrategySO
     * @return
     */
    @PostMapping(value = "/update/strategy", produces = {"application/json"})
    public ResultMessage updateStrategy(@RequestBody UpdateStrategySO updateStrategySO){
        return orderService.updateStrategy(updateStrategySO);
    }



    /**
     * 更新订单收益、收益率及年化率
     */
    @PostMapping(value = "/update/income", produces = {"application/json"})
    public ResultMessage updateOrderIncome(@RequestBody OrderNoSO so){
        log.info("新订单收益、收益率及年化率请求参数: orderNo={}", so.getOrderNo());
        orderService.updateOrderIncome(so.getOrderNo());
        return new ResultMessage(ResultMessage.SUCCEED_CODE,ResultMessage.SUCCEED_MSG);
    }

    /**
     * 接收交易日志推送
     * 请求示例：POST /message/trade {"strategyId": "xxx", "tradeNo": "xxx", ...}
     */
    @PostMapping(value = "/trade", produces = {"application/json"})
    public ResultMessage receiveTradeLog(@RequestBody TradeLogPushSO tradeLog) {
        log.info("接收交易日志推送: {}", JSONObject.fromObject(tradeLog).toString());
        return orderService.receiveTradeLog(tradeLog);
    }

    /**
     * 接收仓位信息推送
     * 请求示例：POST /message/position {"strategyId": "xxx", "tradeBl": "buy", ...}
     */
    @PostMapping(value = "/position", produces = {"application/json"})
    public ResultMessage receivePositionInfo(@RequestBody PositionPushSO position) {
        log.info("接收仓位信息推送:{}", JSONObject.fromObject(position).toString());
        return orderService.receivePositionInfo(position);
    }

    // ==================== 交易所API CRUD ====================

    /**
     * 添加用户交易所API
     * 请求示例：POST /order/exchange/add {"footplate": 0, "type": 0, "name": "币安现货", "apikey": "xxx", "secret": "xxx", "remark": "备注"}
     */
    @PostMapping(value = "/exchange/add", produces = {"application/json"})
    public ResultMessage addApikeyInfo(@RequestBody com.example.core.mainbody.so.AddApikeySO so) {
        UserInfo userInfo = this.getLoginUser();
        log.info("添加交易所API: name={}, userId={}", so.getName(), userInfo.getUserId());
        return orderService.addApikeyInfo(so, userInfo.getUserId());
    }

    /**
     * 更新用户交易所API
     * 请求示例：POST /order/exchange/update {"id": 1, "name": "新名称", "apikey": "xxx", "secret": "xxx"}
     */
    @PostMapping(value = "/exchange/update", produces = {"application/json"})
    public ResultMessage updateApikeyInfo(@RequestBody ApikeyInfo apikeyInfo) {
        UserInfo userInfo = this.getLoginUser();
        log.info("更新交易所API: id={}, userId={}", apikeyInfo.getId(), userInfo.getUserId());
        return orderService.updateApikeyInfo(apikeyInfo, userInfo.getUserId());
    }

    /**
     * 删除用户交易所API
     * 请求示例：POST /order/exchange/delete {"id": 1}
     */
    @PostMapping(value = "/exchange/delete", produces = {"application/json"})
    public ResultMessage deleteApikeyInfo(@RequestBody IdSO so) {
        UserInfo userInfo = this.getLoginUser();
        log.info("删除交易所API: id={}, userId={}", so.getId(), userInfo.getUserId());
        return orderService.deleteApikeyInfo(so.getId(), userInfo.getUserId());
    }

    // ==================== 公开机器人 ====================

    /**
     * 查询公开机器人列表
     * 请求示例：POST /order/robot/public/list {"exchange": 0}
     */
    @PostMapping(value = "/robot/public/list", produces = {"application/json"})
    public ResultMessage queryPublicRobotList(@RequestBody QueryRobotSO so) {
        log.info("查询公开机器人列表: exchange={}", so.getExchange());
        return orderService.queryPublicRobotList(so.getExchange(), so.getPageNum(), so.getPageSize(), so.getSortType());
    }

    /**
     * 查询公开机器人详情
     * 请求示例：POST /order/robot/public/detail {"orderNo": "xxx"}
     * 返回：不含持仓数据，仅保留基础信息与收益数据
     */
    @PostMapping(value = "/robot/public/detail", produces = {"application/json"})
    public ResultMessage queryPublicRobotDetail(@RequestBody OrderNoSO so) {
        log.info("查询公开机器人详情: orderNo={}", so.getOrderNo());
        return orderService.queryPublicRobotDetail(so.getOrderNo());
    }

    /**
     * 设置机器人公开状态
     * 请求示例：POST /order/robot/setPublic {"orderId": 1, "pub": 1}
     */
    @PostMapping(value = "/robot/setPublic", produces = {"application/json"})
    public ResultMessage setRobotPublic(@RequestBody SetRobotPublicSO so) {
        UserInfo userInfo = this.getLoginUser();
        log.info("设置机器人公开状态: orderNo={}, pub={}, userId={}", so.getOrderNo(), so.getPub(), userInfo.getUserId());
        return orderService.setRobotPublic(so.getOrderNo(), so.getPub(), userInfo.getUserId());
    }



    /**
     * 查询用户机器人列表
     * 对应原型图：机器人列表页
     * 请求示例：POST /order/robot/list {"exchange": 0}  (0=币安, 1=Gate, null=全部)
     * 返回：订单列表（包含收益、运行时长、状态等）
     */
    @PostMapping(value = "/robot/list", produces = {"application/json"})
    public ResultMessage queryRobotList(@RequestBody QueryRobotSO so) {
        UserInfo userInfo = this.getLoginUser();
        return orderService.queryRobotList(userInfo.getUserId(), so);
    }

    /**
     * 查询机器人详情
     * 对应原型图：机器人详情页（基础信息+当前仓位）
     * 请求示例：POST /order/robot/detail {"orderNo": "xxx"}
     * 返回：订单基础信息、收益数据、当前持仓信息
     */
    @PostMapping(value = "/robot/detail", produces = {"application/json"})
    public ResultMessage queryRobotDetail(@RequestBody OrderNoSO so) {
        UserInfo userInfo = this.getLoginUser();
        return orderService.queryRobotDetail(userInfo.getUserId(), so.getOrderNo());
    }

    /**
     * 查询历史仓位列表
     * 对应原型图：机器人详情页 - 历史仓位部分
     * 请求示例：POST /order/position/history {"orderId": 3241, "pageNum": 1, "pageSize": 10}
     * 返回：已平仓的历史仓位列表（分页）
     */
    @PostMapping(value = "/position/history", produces = {"application/json"})
    public ResultMessage queryHistoryPositionList(@RequestBody QueryHistoryPositionSO so) {
        return orderService.queryHistoryPositionList(so);
    }


    /**
     * 查询交易记录列表
     * 对应原型图：机器人详情页 - 交易记录部分
     * 请求示例：POST /order/trade/record {"orderId": 3241, "pageNum": 1, "pageSize": 10}
     * 返回：交易记录列表（分页，包含交易所、币对、买卖方向、价格、收益）
     */
    @PostMapping(value = "/trade/record", produces = {"application/json"})
    public ResultMessage queryTradeRecordList(@RequestBody QueryTradeRecordSO so) {
        return orderService.queryTradeRecordList(so);
    }

    /**
     * 接收策略状态心跳上报
     * Python策略定期上报运行状态，Java端同步更新订单状态
     *
     * 请求示例：POST /order/status
     * {
     *   "strategyId": "STRAT-SINGLE",
     *   "orderNo": "xxx",
     *   "status": "running",
     *   "profit": 12.5,
     *   "position": 0.01,
     *   "updateTime": "2026-05-26 10:00:00"
     * }
     */
    @PostMapping(value = "/status", produces = {"application/json"})
    public ResultMessage receiveStrategyStatus(@RequestBody String statusJson) {
        log.info("接收策略状态上报: {}", statusJson);
        return orderService.receiveStrategyStatus(statusJson);
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
    public ResultMessage deleteStrategyInfo(@RequestBody IdSO so) {
        log.info("删除策略模板: id={}", so.getId());
        return orderService.deleteStrategyInfo(so.getId());
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

    /**
     * 查询收益曲线数据
     * 对应原型图：机器人详情页 - 收益曲线
     * 请求示例：POST /order/profit/curve {"orderNo": "xxx"}
     * 返回：按时间节点聚合的收益数组 [{timeLabel: "2024-01", income: 12.5}]
     */
    @PostMapping(value = "/profit/curve", produces = {"application/json"})
    public ResultMessage queryProfitCurve(@RequestBody OrderNoSO so) {
        log.info("查询收益曲线: orderNo={}", so.getOrderNo());
        return orderService.queryProfitCurve(so.getOrderNo());
    }

    // ==================== 策略参数管理 ====================

    /**
     * 查询策略订单参数
     * 请求示例：POST /order/strategy/param/query {"orderNo": "xxx"}
     * 返回：策略参数字符串
     */
    @PostMapping(value = "/strategy/param/query", produces = {"application/json"})
    public ResultMessage queryStrategyParam(@RequestBody OrderNoSO so) {
        log.info("查询策略参数: orderNo={}", so.getOrderNo());
        return orderService.queryStrategyParam(so.getOrderNo());
    }

    /**
     * 更新策略订单参数
     * 请求示例：POST /order/strategy/param/update {"orderNo": "xxx", "paramStr": "{}"}
     * 返回：更新结果（若运行中会自动重启策略使新参数生效）
     */
    @PostMapping(value = "/strategy/param/update", produces = {"application/json"})
    public ResultMessage updateStrategyParam(@RequestBody UpdateStrategyParamSO so) {
        UserInfo userInfo = this.getLoginUser();
        log.info("更新策略参数: orderNo={}, userId={}", so.getOrderNo(), userInfo.getUserId());
        return orderService.updateStrategyParam(so.getOrderNo(), so.getParamStr());
    }


}
