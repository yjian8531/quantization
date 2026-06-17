package com.example.core.mainbody.so.order;

import lombok.Data;

@Data
public class UpdateStrategySO {

    /** 策略订单编号 **/
    private String orderNo;

    /** 策略ID **/
    private String strategyId;

    /** 币对 **/
    private String symbol;

}
