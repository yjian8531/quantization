package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;
/**
 * 产品参数表
 */
@Data
public class ProductParam {
    private Integer id;
    /**
     * 产品ID
     */
    private Integer productId;
    /**
     * 参数分组
     */
    private String paramGroup;
    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数值
     */
    private String paramValue;
    /**
     * 排序
     */
    private Integer sortOrder;
    /**
     * 创建时间
     */
    private Date createTime;


}