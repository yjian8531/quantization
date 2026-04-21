package com.example.core.common.so.finance;

import lombok.Data;

/**
 * 返佣记录查询参数（管理端）
 */
@Data
public class QueryCommissionListSO {
    private Integer pageNum;
    private Integer pageSize;
    private String userId;
    private String email;
    private Integer type;
    private String startTime;
    private String endTime;
}
