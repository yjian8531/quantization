package com.example.core.mainbody.so.profitrecord;

import lombok.Data;

/**
 *  查询用户利润记录的SO
 */
@Data
public class QueryProfitRecordSO {
    /** 产品名称 */
    private String productName;
    /** 产品编号 */
    private String productNo;
    /** 开始时间 */
    private String startTime;
    /** 结束时间 */
    private String endTime;
    /** 当前页码 */
    private Integer pageNum;
    /** 每页数量 */
    private Integer pageSize;
}
