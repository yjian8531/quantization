package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 折扣配置查询参数（管理端）
 */
@Data
public class QueryDiscountListSO {
    private Integer pageNum;
    private Integer pageSize;
    private String userId;
    private String email;
}
