package com.example.core.mainbody.so.product;

import lombok.Data;

/**
 * 根据交易所平台查询策略列表 SO
 */
@Data
public class QueryStrategyByExchangeSO {

    /**
     * 交易所平台筛选条件
     * null = 查询全部
     * 0 = 币安
     * 1 = Gate
     */
    private Integer footplate;

}
