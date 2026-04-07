package com.example.core.mainbody.so.admin;

import lombok.Data;

/**
 * 获取用户活跃数据统计
 */
@Data
public class QueryTotalUserSO {

    /** 时间节点(Y:年,M:月,D:日)**/
    private String dateStyle;

    /** 开始日期 **/
    private String startTime;

    /** 结束日期 **/
    private String endTime;

}
