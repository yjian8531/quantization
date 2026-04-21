package com.example.core.mainbody.so.finance;

import lombok.Data;

@Data
public class QueryFinanceStatisticsListSO {

    /** 开始统计时间 **/
    private String startTime;

    /** 结束统计时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;

}
