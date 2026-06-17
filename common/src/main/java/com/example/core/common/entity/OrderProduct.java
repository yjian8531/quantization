package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单产品信息表
 */
@Data
public class OrderProduct {
    /**
     * 主键 ID
     */
    private Integer id;
    /**
     * 产品编号
     */
    private String productNo;
    /**
     * 产品名称 （如：2 万5 万 USDT）
     */
    private String productName;

    /**
     * 产品等级：1=入门，2=标准，3=高级
     */
    private Integer level;

    /**
     * 预估年化率（%）
     */
    private BigDecimal estimateRate;

    /**
     * 月租金额（USDT）
     */
    private BigDecimal monthlyFee;

    /**
     * 月租比率（%）
     */
    private BigDecimal monthlyRatio;

    /**
     * 最高投入限制（USDT）
     */
    private BigDecimal topLimit;

    /**
     * 最低投入限制（USDT）
     */
    private BigDecimal bottomLimit;

    /**
     * 产品说明
     */
    private String description;

    /**
     * 累计交易额（USDT）
     */
    private BigDecimal totalAmount;

    /**
     * 盈利占比（%）
     */
    private BigDecimal profitRatio;

    /** 产品累计盈利（USDT） */
    private BigDecimal cumulativeProfit;

    /**
     * 已购买人数
     */
    private Integer buyCount;

    /**
     * 状态：0=正常，1=禁用
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
     * 更新时间
     */
    private Date updateTime;


}
