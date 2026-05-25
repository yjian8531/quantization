package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 *  创建充值收款订单SO
 */
@Data
public class CeratePayOrderSO {

    /**
     * 充值金额
     */
    private double amount;

    /**
     * 0:账号余额,1:微信,2:支付宝,3：微信H5支付
     */
    private Integer way;

    /** 订单编号 **/
    private String orderNo;

    /** 周期(月) **/
    private Integer period;

}
