package com.example.core.mainbody.so.finance;

import lombok.Data;

import java.util.List;

/**
 * 查询账单明细列表(用户)SO
 */
@Data
public class QueryDetailListSO {

    /** 0:收入,1:支出 **/
    private Integer direction;
    /**标签**/
    private List<String> tags;
    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;
}
