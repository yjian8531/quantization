package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 产品信息表
 */
@Data
public class ProductInfo {
    private Integer id;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 产品等级
     */
    private Integer level;
    /**
     * 年化利率
     */
    private BigDecimal annualRate;
    /**
     * 月租金额
     */
    private BigDecimal monthlyFee;
    /**
     * 已购买人数
     */
    private Integer buyCount;
    /**
     * 累计交易额
     */
    private BigDecimal totalAmount;
    /**
     * 盈利占比
     */
    private BigDecimal profitRatio;
    /**
     * 产品说明
     */
    private String description;
    /**
     * 累计盈利
     */
    private BigDecimal cumulativeProfit;
    /**
     * 状态：0=上架，1=下架
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;

}