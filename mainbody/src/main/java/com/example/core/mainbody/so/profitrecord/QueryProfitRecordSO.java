package com.example.core.mainbody.so.profitrecord;

import lombok.Data;

/**
 * 查询用户利润记录的SO
 */
@Data
public class QueryProfitRecordSO {
    
    private String productName;
    
    private String startTime;
    
    private String endTime;
    
    private Integer pageNum;
    
    private Integer pageSize;
}
