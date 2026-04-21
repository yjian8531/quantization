package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
//充值订单表
@Data
public class DepositOrder {
    private Integer id;

    //订单号
    private String orderNo;
    //用户id
    private String userId;
    //充值地址
    private String address;
    //网络类型
    private String networkType;
    //充值金额
    private BigDecimal amount;
    //充值状态
    private Integer status;
    //交易hash
    private String txHash;
    //创建时间
    private Date createTime;
    //确认时间
    private Date confirmTime;

}