package com.example.core.common.vo.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 查询客户推广统计
 */
@Data
public class QueryCommissionStatisticsVO {

    /** 推广用户总数 **/
    private Integer userNum;

    /** 产品总数 **/
    private Integer productNum;

    /** 消费总额 **/
    private BigDecimal consumptionTotal;

    /** 返佣总额 **/
    private BigDecimal commissionTotal;

    /** 用户ID **/
    private String userId;

    /** 用户账号 **/
    private String account;

    /** 备注信息 **/
    private String remark;

}
