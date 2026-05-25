package com.example.core.common.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户财务明细
 */
@Data
public class FinanceDetail {
    
    /** 主键id **/
    private Integer id;
    
    /** 用户id **/
    private String userId;
    
    /** 账单编号 **/
    private String financeNo;
    
    /** 订单编号 **/
    private String orderNo;
    
    /** 类型(0:充值,1:消费,2:提现,3:其他) **/
    private Integer type;
    
    /** 交易金额 **/
    private BigDecimal moneyNum;
    
    /** 周期 **/
    private Integer period;
    
    /** 标签(topup:充值,buy:购买,commission:佣金,withdraw:提现,renew:续费) **/
    private String tag;
    
    /** 方向(0:收入,1:支出) **/
    private Integer direction;
    
    /** 交易方式(0:账号余额,1:微信,2:支付宝,3:信用卡) **/
    private Integer way;
    
    /** 状态(0:进行中,1:完成,2:取消) **/
    private Integer status;
    
    /** 备注 **/
    private String remarks;
    /** 交易哈希（区块链交易ID） **/
    private String txHash;

    /** 币种类型 **/
    private String coinType;

    /** 链网络类型 **/
    private String chainType;
    
    /** 创建时间 **/
    private Date createTime;
    
    /** 最后更新时间 **/
    private Date updateTime;

}
