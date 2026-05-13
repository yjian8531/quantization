package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;
/**
 * 用户钱包信息表
 */
@Data
public class FinancialWallet {
    private Integer id;

    // 用户ID
    private String userId;
    // 钱包类型(ERC20,BEP20,TRC20)
    private String type;
    // 钱包地址
    private String address;
    // 钱包状态(0:正常,1:禁用)
    private Integer status;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;

}