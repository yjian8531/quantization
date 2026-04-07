package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 查询用户操作日志SO
 */
@Data
public class QueryUserLogListSO {

    /** 操作名称 **/
    private String alias;
    private Integer pageNum;
    private Integer pageSize;
}
