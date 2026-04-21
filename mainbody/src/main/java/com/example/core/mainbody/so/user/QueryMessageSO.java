package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 查询消息列表参数
 */
@Data
public class QueryMessageSO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Integer type;
}
