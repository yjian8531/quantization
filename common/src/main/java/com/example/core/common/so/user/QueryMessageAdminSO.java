package com.example.core.common.so.user;

import lombok.Data;

/**
 * 消息查询参数（管理端）
 */
@Data
public class QueryMessageAdminSO {
    // 分页参数
    private Integer pageNum = 1;
    // 分页参数
    private Integer pageSize = 10;
    // 用户id
    private String userId;
    // 消息类型
    private Integer type;
    // 是否已读
    private Integer isRead;
}
