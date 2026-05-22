package com.example.core.mainbody.so.profitrecord;

import lombok.Data;

/**
 * 查询收益趋势 SO
 */
@Data
public class QueryProfitTrendSO {
    
    /** 时间维度：year-年, month-月, day-天 */
    private String dimension;
    
    /** 开始日期（可选，不传则默认最近 1 年/ 1月/7 天） */
    private String startTime;
    
    /** 结束日期（可选） */
    private String endTime;
}
