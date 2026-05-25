package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 系统消息实体类
 * 用于存储系统消息的相关信息
 */
@Data
public class SystemMessage {
    //主键
    private Integer id;
    // 用户ID
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