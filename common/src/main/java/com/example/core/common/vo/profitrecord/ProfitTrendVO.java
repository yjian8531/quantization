package com.example.core.common.vo.profitrecord;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 收益趋势数据 VO（用于折线图）
 */
@Data
public class ProfitTrendVO {
    
    /** 时间标签（如 "2023"、"2023-01"、"2023-01-01"） */
    private String timeLabel;
    
    /** 该时间段的总收益 */
    private BigDecimal totalProfit;
}
