package com.example.core.common.vo.robot;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 公开机器人详情VO（仅对外展示字段）
 */
@Data
public class PublicRobotDetailVO {

    /** 订单编号 */
    private String orderNo;

    /** 订单名称 */
    private String orderName;

    /** 投资者昵称 */
    private String investorName;

    /** 交易所名称 */
    private String exchangeName;

    /** 状态：0=启动中，1=运行中，2=暂停，3=已结束 */
    private Integer status;

    /** 状态名称 */
    private String statusName;

    /** 创建时间 */
    private String createTime;

    /** 运行时间（格式化：34天3小时29分） */
    private String runTime;

    /** 最后更新时间 */
    private String updateTime;

    /** 投入金额 */
    private BigDecimal investAmount;

    /** 历史成交笔数 */
    private Integer tradeCount;

    /** 预估年化率 */
    private BigDecimal annualizedRate;

    /** 收益金额 */
    private BigDecimal income;

    /** 收益率 */
    private BigDecimal incomeRate;
}
