package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 佣金明细表
 */
@Data
public class CommissionDetail {
    private Integer id;

    private String userId;

    private String lowUserId;

    private Integer type;

    private String productNo;

    private String orderNo;

    private BigDecimal price;

    private BigDecimal consumption;

    private BigDecimal commission;

    private BigDecimal ratio;

    private Date createTime;

    private Date updateTime;

}