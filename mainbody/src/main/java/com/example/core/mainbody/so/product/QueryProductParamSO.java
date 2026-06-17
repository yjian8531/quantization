package com.example.core.mainbody.so.product;

import lombok.Data;

/**
 * 查询产品策略参数 SO
 */
@Data
public class QueryProductParamSO {
    /** 产品ID */
    private Integer productId;

    /** 策略ID */
    private String strategyId;
}
