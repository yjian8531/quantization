package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 查询用户返佣明细SO
 */
@Data
public class QueryCommissionDetailListSO {

    private String userId;

    /** 手机或邮箱 **/
    private String account;

    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;
}
