package com.example.core.common.vo.robot;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 历史仓位VO
 * 对应原型图：历史仓位列表项
 */
@Data
public class HistoryPositionVO {

    /** 仓位ID */
    private Integer id;

    /** 订单编号 */
    private String orderNo;

    /** 交易方向（buy=多单/sell=空单） */
    private String tradeBl;

    /** 交易方向名称 */
    private String tradeBlName;

    /** 开仓价格 */
    private BigDecimal openPrice;

    /** 平仓价格 */
    private BigDecimal closePrice;

    /** 交易数量 */
    private BigDecimal tradeNum;

    /** 收益金额 */
    private BigDecimal income;

    /** 收益率（%） */
    private BigDecimal incomeRate;

    /** 开始时间 */
    private String startTime;

    /** 结束时间 */
    private String endTime;
}
