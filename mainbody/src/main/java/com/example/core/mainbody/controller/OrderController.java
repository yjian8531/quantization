package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.so.product.ConfigRobotSO;
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
     * 配置机器人（创建策略订单）
     * 对应原型图：产品详情页 -> 点击"创建"按钮
     * 功能：
     * 1. 校验产品和余额
     * 2. 冻结余额 -> 创建订单 -> 实际扣款
     * 3. 生成策略订单记录
     */
    @PostMapping(value = "/configure", produces = {"application/json"})
    public ResultMessage configRobot(@RequestBody ConfigRobotSO so) {
        String userId = getUserId();
        return orderService.configRobot(userId, so);
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
}
