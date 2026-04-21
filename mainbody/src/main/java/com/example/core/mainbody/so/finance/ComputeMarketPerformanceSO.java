package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 计算市场部提成
 */
@Data
public class ComputeMarketPerformanceSO {
    /** 月份(2023-13) **/
    private String monthStr;

    /** 用户ID **/
    private String userId;

    private Integer pageNum;

    private Integer pageSize;
}
