package com.example.core.mainbody.so.admin;

import lombok.Data;

@Data
public class QuerySummaryListSO {

    /** 时间节点(Y:年,M:月,D:日)**/
    private String dateStyle;

    /** 开始日期 **/
    private String startTime;

    /** 结束日期 **/
    private String endTime;
}
