package com.example.core.mainbody.so.admin;

import lombok.Data;

/**
 * 查询登录日志SO
 */
@Data
public class QueryAdminLogSO {

    /** 账号 **/
    private String account;
    /** 登录时间开始 **/
    private String startTime;
    /** 登录时间结束 **/
    private String endTime;
    private Integer pageNum;
    private Integer pageSize;

}
