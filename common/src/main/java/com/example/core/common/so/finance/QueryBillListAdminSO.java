package com.example.core.common.so.finance;

import lombok.Data;

/**
 * 财务明细查询参数（管理端）
 */
@Data
public class QueryBillListAdminSO {
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 0:收入 1:支出
     */
    private Integer direction;
    /**
     * 标签
     */
    private String tag;
    /**
     * 时间范围
     */
    private String startTime;
    /**
     * 时间范围
     */
    private String endTime;
}
