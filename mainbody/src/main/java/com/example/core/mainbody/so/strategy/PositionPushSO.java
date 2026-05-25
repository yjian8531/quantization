package com.example.core.mainbody.so.strategy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  仓位信息推送数据
 * 用于接收Python策略脚本推送的仓位信息
 */
@Data
public class PositionPushSO {

    /**
     * 策略ID
     */
    private String strategyId;

    /**
     * 交易方向(buy:多单/sell:空单)
     */
    private String tradeBl;

    /**
     * 开仓金额
     */
    private BigDecimal openPrice;

    /**
     * 平仓金额
     */
    private BigDecimal closePrice;

    /**
     * 交易数量
     */
    private BigDecimal tradeNum;

    /**
     * 收益金额
     */
    private BigDecimal income;

    /**
     * 收益率（%）
     */
    private BigDecimal incomeRate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 关闭时间
     */
    private Date endTime;

}
