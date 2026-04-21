package com.example.core.common.so.finance;

import lombok.Data;

/**
 * 财务明细查询参数（管理端）
 */
@Data
public class QueryBillListAdminSO {
    private Integer pageNum;
    private Integer pageSize;
    private String userId;
    private String email;
    private Integer direction;
    private String tag;
    private String startTime;
    private String endTime;
}
