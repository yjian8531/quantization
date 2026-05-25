package com.example.core.mainbody.so.order;


import lombok.Data;

@Data
public class QueryOrderTaskListSO {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private Integer status;
    
    private Integer tag;
}
