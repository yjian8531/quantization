package com.example.core.mainbody.service;

import com.example.core.common.entity.StrategyInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
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
     * 接收策略状态心跳
     */
    ResultMessage receiveStrategyStatus(String statusJson);

    /**
     * 查询收益曲线数据
     */
    ResultMessage queryProfitCurve(String userId, Integer orderId);

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


    /** 新增策略模板 */
    ResultMessage addStrategyInfo(StrategyInfo strategyInfo);

    /** 修改策略模板 */
    ResultMessage updateStrategyInfo(StrategyInfo strategyInfo);

    /** 删除策略模板 */
    ResultMessage deleteStrategyInfo(Integer id);

    /** 根据策略ID查询策略模板详情 */
    ResultMessage queryStrategyInfo(String strategyId);

    /**  查询策略模板列表 */
    ResultMessage queryStrategyInfoList();

}
