package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 币对列表
 */
@Data
public class SymbolInfo {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 币对
     */
    private String symbol;

    /**
     * 真实值
     */
    private BigDecimal realVal;

    /**
     * 状态 (0:正常,1:禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

}
