package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;
/**
 * 产品参数 表
 */
@Data
public class ProductParam {
    private Integer id;
    /**
     * 产品ID
     */
    private Integer productId;
    /**
     * 策略ID
     */
    private String strategyId;
    /**
     * 参数分组：basic=基础配置，position=仓位配置
     */
    private String paramGroup;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数Key
     */
    private String paramKey;
    /**
     * 参数值
     */
    private String paramValue;
    /**
     * 描述
     */
    private String describe;
    /**
     * 单位
     */
    private String unit;
    /**
     * 排序(数值越小越前面)
     */
    private Integer sortOrder;
    /**
     * 创建时间
     */
    private Date createTime;


}