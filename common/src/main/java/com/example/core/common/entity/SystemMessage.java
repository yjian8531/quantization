package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 系统消息
 */
@Data
public class SystemMessage {
    private Integer id;

    private String userId;
    // 标题
    private String title;
    // 内容
    private String content;
    // 类型
    private Integer type;
    // 是否已读
    private Integer isRead;
    // 创建时间
    private Date createTime;
    // 阅读时间
    private Date readTime;


}