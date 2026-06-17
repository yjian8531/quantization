package com.example.core.common.vo.robot;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 机器人详情VO
 * 对应原型图：机器人详情页顶部信息卡+当前仓位
 */
@Data
public class RobotDetailVO {

    // ===== 基础信息 =====
    /** 订单ID */
    private Integer id;

    /** 订单编号 */
    private String orderNo;

    /** 订单名称 */
    private String orderName;

    /** 投资者昵称 */
    private String investorName;

    /** 投资者ID */
    private String userId;

    /** 服务器IP */
    private String serverIp;

    /** 交易所API ID */
    private Integer apikeyId;

    /** 交易所API名称 */
    private String apikeyName;

    /** 币对 */
    private String symbol;

    /** 交易所名称 */
    private String exchangeName;

    /** 状态：0=启动中，1=运行中，2=暂停，3=已结束 */
    private Integer status;

    /** 状态名称 */
    private String statusName;

    /** 创建时间 */
    private String createTime;

    /** 运行时间（格式化：34天3小时29分） */
    private String runTime;

    /** 运行天数（纯数字） */
    private Integer runDays;

    /** 最后更新时间 */
    private String updateTime;

    /** 投入金额 */
    private BigDecimal investAmount;

    /** 历史成交笔数 */
    private Integer tradeCount;

    /** 预估年化率 */
    private BigDecimal annualizedRate;

    /** 收益金额 */
    private BigDecimal income;

    /** 收益率 */
    private BigDecimal incomeRate;

    /** 策略ID */
    private String strategyId;

    /** 策略名称 */
    private String strategyName;

    // ===== 当前仓位 =====
    /** 持仓数量 */
    private BigDecimal positionNum;

    /** 持仓均价 */
    private BigDecimal positionAvgPrice;

    /** 未实现盈亏 */
    private BigDecimal unrealizedPnl;

    /** 强平价格 */
    private BigDecimal liquidationPrice;

    /** 杠杆倍数 */
    private BigDecimal leverage;

    /** 止赢价格 */
    private BigDecimal takeProfitPrice;

    /** 开始时间 */
    private String positionStartTime;

    /** 最后更新时间 */
    private String positionUpdateTime;
}
