package com.example.core.common.vo.product;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 产品列表 VO（用于前端展示产品卡片）
 * 对应原型图：产品精选列表页的每个产品卡片
 */
@Data
public class ProductListVO {

    /** 产品 ID */
    private Integer id;

    /** 产品名称（如：2 万～5 万 USDT） */
    private String productName;

    /** 产品等级：1=入门，2=标准，3=高级 */
    private Integer level;

    /** 历史年化率（百分比，如 15 表示 15%） */
    private BigDecimal annualRate;

    /** 月租金额（USDT） */
    private BigDecimal monthlyFee;

    /** 月租比率（%） */
    private BigDecimal monthlyRatio;

    /** 已购买人数 */
    private Integer buyCount;

    /** 累计交易额（USDT） */
    private BigDecimal totalAmount;

    /** 盈利占比（百分比，如 80 表示 80%） */
    private BigDecimal profitRatio;
}
