package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 查询用户推广明细SO
 */
@Data
public class QueryUserProListSO {
    /** 用户ID **/
    private String userId;

    /** 手机获邮箱 **/
    private String account;

    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;

}
