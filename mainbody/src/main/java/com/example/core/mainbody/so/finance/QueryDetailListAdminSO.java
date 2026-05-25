package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 *  查询财务账单明细SO
 */
@Data
public class QueryDetailListAdminSO {

    /** 账号 **/
    private String account;

    /** 类型(0:充值,1:消费,2:提现) **/
    private Integer type;

    /** 交易方式(0:支付宝,1:微信,2:账号余额) **/
    private Integer way;

    /** 标签(topup:充值,buy:购买,commission:佣金,withdraw:提现,renew:续费,manage:人工操作) **/
    private String tag;

    /** 0:收入,1:支出 **/
    private Integer direction;

    /** 状态(0:进行中,1:完成) **/
    private Integer status;

    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    /** 账单特性(0:HK对公,1:CN对私) **/
    private Integer features;

    private Integer pageNum;

    private Integer pageSize;

}
