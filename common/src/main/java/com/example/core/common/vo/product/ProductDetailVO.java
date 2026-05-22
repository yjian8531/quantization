package com.example.core.common.vo.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 产品详情 VO
 * 对应原型图：产品详情页弹窗
 */
@Data
public class ProductDetailVO {

    /** 产品 ID */
    private Integer id;

    /** 产品名称 */
    private String productName;

    /** 产品等级：1=入门，2=标准，3=高级 */
    private Integer level;

    /** 预估年化率（百分比） */
    private BigDecimal estimateRate;

    /** 产品累计盈利（USDT） */
    private BigDecimal cumulativeProfit;


    /** 月租金额（USDT） */
    private BigDecimal monthlyFee;

    /** 产品说明文案 */
    private String description;

    /** 参数配置（解析后的表格结构） */
    private List<ParamConfigGroupVO> paramConfigList;
}
