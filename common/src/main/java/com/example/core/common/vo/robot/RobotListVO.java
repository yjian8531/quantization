package com.example.core.common.vo.robot;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RobotListVO {

    /** 订单ID */
    private Integer id;

    /** 订单编号 */
    private String orderNo;

    /** 订单名称 */
    private String orderName;

    /** 投资者昵称 */
    private String investorName;

    /** 交易所名称 */
    private String exchangeName;

    /** 币对 */
    private String symbol;

    /** 预估年化率 */
    private BigDecimal annualizedRate;

    /** 收益金额 */
    private BigDecimal income;

    /** 状态：0=启动中，1=运行中，2=暂停，3=已结束 */
    private Integer status;

    /** 运行天数 */
    private Integer runDays;

    /** 创建时间 */
    private String createTime;
}