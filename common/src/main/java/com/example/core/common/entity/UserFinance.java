package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class UserFinance {
    private Integer id;

    /** 用户ID**/
    private String userId;

    /** 总资产**/
    private BigDecimal totalNum;

    /** 可用余额**/
    private BigDecimal validNum;

    /** 冻结金额**/
    private BigDecimal frozenNum;

    /** 类型**/
    private Integer type;

    /** 创建时间**/
    private Date createTime;

    /** 更新时间**/
    private Date updateTime;

}