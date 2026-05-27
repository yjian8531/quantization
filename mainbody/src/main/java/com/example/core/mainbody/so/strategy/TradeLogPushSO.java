package com.example.core.mainbody.so.strategy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 *  交易日志推送数据
 * 用于接收Python策略脚本推送的交易信息
 */
@Data
public class TradeLogPushSO {

    /**
     * 策略ID
     */
    private String orderNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易方向(openBuy:买入开多/openSell:卖出开空/closeBuy:卖出平多/closeSell:买入平空)
     */
    private String tradeBl;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 交易数量
     */
    private BigDecimal tradeNum;

    /**
     * 收益
     */
    private BigDecimal income;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private String createTime;


}
