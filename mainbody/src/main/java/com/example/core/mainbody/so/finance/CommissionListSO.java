package com.example.core.mainbody.so.finance;

import lombok.Data;

@Data
public class CommissionListSO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 类型
     */
    private Integer type;
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 每页数量
     */
    private Integer pageSize;
}
