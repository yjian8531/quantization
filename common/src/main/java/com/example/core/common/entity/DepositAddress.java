package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;
//充值地址表
@Data
public class DepositAddress {
    private Integer id;
    //充值地址
    private String address;
    //网络类型
    private String networkType;
    //用户id
    private String userId;
    //绑定时间
    private Date bindTime;


}