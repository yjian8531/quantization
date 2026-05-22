package com.example.core.mainbody.service;

import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {

    /**
<<<<<<< HEAD
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

=======
     * 配置机器人（创建策略订单）
     */
    ResultMessage configRobot(String userId, ConfigRobotSO so);

    /**
     * 查询用户机器人列表
     */
    ResultMessage queryRobotList(String userId, QueryRobotSO so);



    /**
     * 查询机器人详情
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    ResultMessage queryRobotDetail(String userId, Integer orderId);



    /**
     * 查询历史仓位列表
     * @param userId 用户ID
     * @param so 查询参数（包含订单ID）
     */
    ResultMessage queryHistoryPositionList(String userId, com.example.core.mainbody.so.robot.QueryHistoryPositionSO so);




    /**
     * 查询交易记录列表
     * @param userId 用户ID
     * @param so 查询参数（包含订单ID）
     */
    ResultMessage queryTradeRecordList(String userId, com.example.core.mainbody.so.robot.QueryTradeRecordSO so);



    /**
     * 查询用户可用交易所列表
     */
    ResultMessage queryExchangeList(String userId);



    /**
     * 查询可用币对列表
     */
    ResultMessage querySymbolList();


    // ========== TODO: 待实现接口 ==========

    /**
     * 重启订单
     * TODO: 需要实现
     * 流程：
     * 1. 校验订单状态（只能重启已暂停status=2或已停止status=3的订单）
     * 2. 调用量化服务器重启API（需要mainNo/serverIp + 订单号）
     * 3. 等待API响应成功后，更新order_info.status = 1（运行中）
     * 4. 清空ent_time结束时间
     * 待确认：量化服务器API地址、请求参数、鉴权方式
     */
//    ResultMessage restartOrder(String userId, Integer orderId);

    /**
     * 停止订单
     * TODO: 需要实现
     * 流程：
     * 1. 校验订单状态（只能停止运行中status=1或启动中status=0的订单）
     * 2. 调用量化服务器停止API（需要mainNo/serverIp + 订单号）
     * 3. 等待API响应成功后，更新order_info.status = 3（已结束）
     * 4. 设置ent_time = NOW() 记录结束时间
     * 待确认：量化服务器API地址、请求参数、鉴权方式
     */
//    ResultMessage stopOrder(String userId, Integer orderId);
>>>>>>> origin/main
}
