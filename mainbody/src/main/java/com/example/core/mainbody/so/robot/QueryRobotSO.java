package com.example.core.mainbody.so.robot;

import lombok.Data;

@Data
public class QueryRobotSO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 交易所筛选：null=全部，0=币安，1=波场 */
    private Integer exchange;
}