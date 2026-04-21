package com.example.core.mainbody.so.finance;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 添加财务统计记录SO
 */
@Data
public class AddFinanceStatisticsSO {

    /** 总收入 **/
    private BigDecimal incomeTotal;

    /** 总毛利 **/
    private BigDecimal incomeProfit;

    /** 总采购支出 **/
    private BigDecimal expenPurchase;

    /** 总退款 **/
    private BigDecimal expenRefund;

    /** 渠道佣金 **/
    private BigDecimal channelCommission;

    /** 销售提成 **/
    private BigDecimal saleCommission;

    /** 市场推广支出 **/
    private BigDecimal expenMarket;

    /** 总手续费 **/
    private BigDecimal feeTotal;

    /** 总净利润 **/
    private BigDecimal profitTotal;

    /** 开始统计时间 **/
    private String startTime;

    /** 结束统计时间 **/
    private String endTime;

    /** 备注 **/
    private String remark;

}
