package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
/**
 * 钱包充值记录表
 */
@Data
public class WalletIncome {
    private Integer id;
    // 用户ID
    private String userId;
    // 钱包类型(ERC20,BEP20,TRC20)
    private String type;
    //区块高度
    private Long block;
    //交易hash
    private String hash;
    //发送地址
    private String fromAddress;
    //接收地址
    private String toAddress;
    //金额
    private BigDecimal amount;
    //手续费
    private BigDecimal gasPrice;
    //手续费上限
    private BigDecimal gasLimit;
    //确认数
    private Integer confirmations;
    //状态 (0:待确认,1:已确认,2:失败)
    private Integer status;
    //备注
    private String remark;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

}