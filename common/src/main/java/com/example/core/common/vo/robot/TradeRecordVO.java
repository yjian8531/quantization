package com.example.core.common.vo.robot;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易记录VO
 * 对应原型图：交易记录列表项
 */
@Data
public class TradeRecordVO {

    /** 交易ID */
    private Integer id;

    /** 订单编号 */
    private String orderNo;

    /** 交易单号 */
    private String tradeNo;

    /** 交易方向（交易方向(openBuy:买入开多/openSell:卖出开空/closeBuy:卖出平多/closeSell:买入平空) */
    private String tradeBl;

    /** 交易方向名称 */
    private String tradeBlName;

    /** 交易所名称 */
    private String exchangeName;

    /** 币对 */
    private String symbol;

    /** 交易金额 */
    private BigDecimal amount;

    /** 交易数量 */
    private BigDecimal tradeNum;

    /** 价格 */
    private BigDecimal price;

    /** 收益 */
    private BigDecimal income;

    /** 创建时间 */
    private String createTime;
}
