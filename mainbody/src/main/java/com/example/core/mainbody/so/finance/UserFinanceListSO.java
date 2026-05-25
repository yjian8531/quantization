package com.example.core.mainbody.so.finance;


import lombok.Data;

/**
 * 用户财务列表查询请求对象
 */
@Data
public class UserFinanceListSO {
    /** 页码 */
    private Integer pageNum;
    /** 每页数量 */
    private Integer pageSize;
    /** 用户ID（可选） */
    private String userId;
}
