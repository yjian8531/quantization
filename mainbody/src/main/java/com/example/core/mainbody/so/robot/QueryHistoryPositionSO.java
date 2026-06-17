package com.example.core.mainbody.so.robot;

import lombok.Data;

/**
 *  历史仓位查询SO
 */
@Data
public class QueryHistoryPositionSO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 订单编号 */
    private String orderNo;
}