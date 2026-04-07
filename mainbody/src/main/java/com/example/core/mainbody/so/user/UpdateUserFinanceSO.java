package com.example.core.mainbody.so.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新用户余额
 */
@Data
public class UpdateUserFinanceSO {

    private String userId;

    /** 标签(add：添加余额,minus：减去余额,unbind：解冻余额,seal：冻结余额) **/
    private String tad;

    /** 操作类型(0:充值,1:退款补偿,2:内部划转(添加),3:佣金,4:消费,5:线下提现,6:内部划转(扣除)) **/
    private Integer type;

    /** 金额 **/
    private BigDecimal moneyNum;

    /** 备注 **/
    private String remark;

}
