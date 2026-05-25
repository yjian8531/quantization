package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 *  查询用户消费列表SO
 */
@Data
public class QueryConsumeListSO {

    /** buy:购买,renew:续费 **/
    private String tag;

    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;

}
