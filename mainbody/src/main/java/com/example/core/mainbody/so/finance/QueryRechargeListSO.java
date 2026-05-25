package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 查询充值列表SO
 */
@Data
public class QueryRechargeListSO {

     /** 币种类型 BSC TRX */
    private String coinType;
    /** 链类型 (TRC20/BEP20) */
    private String chainType;
    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;
}
