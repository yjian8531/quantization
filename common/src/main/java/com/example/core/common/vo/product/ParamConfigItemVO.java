package com.example.core.common.vo.product;

import lombok.Data;

/**
 * 参数配置项 VO
 */
@Data
public class ParamConfigItemVO {

    /** 显示名称（如：本金、初始开仓金额） */
    private String name;

    /** 字段标识Key（如：AccountAmount、FirstOrderAmount） */
    private String key;

    /** 参数值 */
    private String value;

    /** 单位（如：USDT、倍、%） */
    private String unit;

    /** 参数描述 */
    private String describe;
}