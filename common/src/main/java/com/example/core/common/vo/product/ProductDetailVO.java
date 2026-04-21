package com.example.core.common.vo.product;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    /** 历史年化率（百分比） */
    private BigDecimal annualRate;

    /** 月租金额（USDT） */
    private BigDecimal monthlyFee;

    /** 产品累计盈利（USDT） */
    private BigDecimal cumulativeProfit;

    /** 产品说明文案 */
    private String description;

    /** 基础参数列表（键值对：如 资金配置: 50000 USDT） */
    private List<Map<String, String>> basicParams;

    /** 仓位配置列表（键值对：如 第一次加仓比率: 0.5%） */
    private List<Map<String, String>> positionParams;
}
