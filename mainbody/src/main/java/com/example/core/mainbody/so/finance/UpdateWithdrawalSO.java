package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 提现审核SO
 */
@Data
public class UpdateWithdrawalSO {
     /** 提现编号 **/
    private String withdrawalNo;

    /** 审核结果(Y:通过/N:驳回) **/
    private String tag;

    /** 备注 **/
    private String remark;

    /** 收款账号 **/
    private String account;

    /** 收款姓名 **/
    private String name;

    /** 收款方式(0:支付宝,1:微信,2:银行卡) **/
    private Integer way;

}
