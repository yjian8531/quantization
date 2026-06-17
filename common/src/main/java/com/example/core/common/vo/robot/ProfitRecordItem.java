package com.example.core.common.vo.robot;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收益记录项（按时间节点汇总）
 * 公开机器人列表中的收益曲线数据点
 */
@Data
public class ProfitRecordItem {

    /** 时间标签（月维度："2024-01"，日维度："2024-01-15"） */
    private String timeLabel;

    /** 该时间节点汇总收益 */
    private BigDecimal income;
}
