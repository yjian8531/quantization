package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 查询充值列表SO
 */
@Data
public class QueryRechargeListSO {

    /** 交易方式(0:支付宝,1:微信,2:账号余额) **/
    private Integer way;

    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;
}
