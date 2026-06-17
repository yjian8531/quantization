package com.example.core.mainbody.service;

import com.example.core.common.entity.ApikeyInfo;
import com.example.core.common.entity.StrategyInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.AddApikeySO;
import com.example.core.mainbody.so.order.UpdateStrategySO;
import com.example.core.mainbody.so.order.UpdateStrategyTagSO;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {

    /**
     * 策略参数风控评估
     * @param paramStr
     * @return
     */
    ResultMessage  checkStrategyParam(String paramStr);

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
     * 重启策略
     * @param orderNo
     * @return
     */
    ResultMessage restartStrategyOrder(String orderNo);


    /**
     * 更新策略平仓
     * @param updateStrategyTagSO
     * @return
     */
    ResultMessage updateStrategyTag(UpdateStrategyTagSO updateStrategyTagSO);

    /**
     * 更换策略和币对
     * @param updateStrategySO
     * @return
     */
    ResultMessage updateStrategy(UpdateStrategySO updateStrategySO);


    /**
     * 接收策略状态心跳
     */
    ResultMessage receiveStrategyStatus(String statusJson);

    /**
     * 查询收益曲线数据
     */
    ResultMessage queryProfitCurve(String orderNo);

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
     * 更新订单收益、收益率及年化率
     */
    void updateOrderIncome(String orderNo);


    /**
     * 查询用户机器人列表
     */
    ResultMessage queryRobotList(String userId, QueryRobotSO so);



    /**
     * 查询机器人详情
     * @param userId 用户ID
     * @param orderNo 订单ID
     */
    ResultMessage queryRobotDetail(String userId, String orderNo);



    /**
     * 查询历史仓位列表
     * @param so 查询参数（包含订单ID）
     */
    ResultMessage queryHistoryPositionList(com.example.core.mainbody.so.robot.QueryHistoryPositionSO so);


    /**
     * 查询交易记录列表
     * @param so 查询参数（包含订单ID）
     */
    ResultMessage queryTradeRecordList(com.example.core.mainbody.so.robot.QueryTradeRecordSO so);


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

    /** 添加用户交易所API */
    ResultMessage addApikeyInfo(AddApikeySO so, String userId);

    /** 更新用户交易所API */
    ResultMessage updateApikeyInfo(ApikeyInfo apikeyInfo, String userId);

    /** 删除用户交易所API */
    ResultMessage deleteApikeyInfo(Integer id, String userId);

    /** 查询公开机器人列表 */
    ResultMessage queryPublicRobotList(Integer exchange, Integer pageNum, Integer pageSize, String sortType);

    /** 用户设置机器人公开或不公开 */
    ResultMessage setRobotPublic(String orderNo, Integer pub, String userId);

    /** 查询公开机器人详情 */
    ResultMessage queryPublicRobotDetail(String orderNo);

    /** 根据策略订单编号查询参数 */
    ResultMessage queryStrategyParam(String orderNo);

    /** 更新策略参数 */
    ResultMessage updateStrategyParam(String orderNo, String paramStr);

}
