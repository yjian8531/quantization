package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 策略订单交易日志
 */
@Data
public class OrderTrade {
    /**
     * 主键 ID
     */
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易方向(buy:买/sell:卖)
     */
    private String tradeBl;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 交易数量
     */
    private BigDecimal tradeNum;

    /**
     * 收益
     */
    private BigDecimal income;

    /**
     * 价格
     */
    private BigDecimal price;

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


}
