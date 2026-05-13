package com.example.core.mainbody.so.robot;

import lombok.Data;

/**
 * 交易记录查询SO
 */
@Data
public class QueryTradeRecordSO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 订单ID */
    private Integer orderId;
}
