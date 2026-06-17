package com.example.core.mainbody.so.order;

import lombok.Data;

@Data
public class UpdateStrategyTagSO {

    /** 策略订单编号 **/
    private String orderNo;

    /** 循环标记(0=正常循序,1=强制平仓,2=保本平仓,3=止赢平仓) **/
    private Integer tag;
}
