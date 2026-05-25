package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 策略订单历史仓位
 */
@Data
public class OrderPosition {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 交易方向 (buy:多单/sell:空单)
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
