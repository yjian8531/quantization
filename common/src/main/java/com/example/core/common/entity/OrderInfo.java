package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 策略订单表
 */
@Data
public class OrderInfo {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单名称
     */
    private String orderName;

    /**
     * 主机编号
     */
    private String mainNo;

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 交易所ApiKeyID
     */
    private Integer apikeyId;

    /**
     * 币对
     */
    private String symbol;

    /**
     * K线时间节点(分钟)
     */
    private String nodeTime;

    /**
     * 策略参数
     */
    private String paramStr;

    /**
     * 收益金额
     */
    private BigDecimal income;

    /**
     * 收益率（%）
     */
    private BigDecimal incomeRate;

    /**
     * 预估年化率（%）
     */
    private BigDecimal annualizedRate;

    /**
     * 状态：0=启动中，1=运行中，2=暂停，3=已结束
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 结束时间
     */
    private Date entTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 策略ID（对应StrategyInfo.strategyId，策略模板标识）
     */
    private String strategyId;

    /**
     * Python策略进程PID
     */
    private String pid;

}
