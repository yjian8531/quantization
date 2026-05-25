package com.example.core.mainbody.service;

import com.example.core.common.entity.SystemMessage;
import com.example.core.common.so.user.QueryMessageAdminSO;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.user.QueryMessageSO;

public interface MessageService {
    /**
     * 查询未读消息数量
     */
    ResultMessage queryUnreadCount(String userId);
    /**
     * 查询消息列表
     */
    ResultMessage queryMessageList(String userId, QueryMessageSO so);
    /**
     * 标记消息为已读
     */
    ResultMessage markAsRead(String userId, Integer messageId);

    /** 查询消息列表（管理端） */
    ResultMessage queryMessageListForAdmin(QueryMessageAdminSO so);

    /** 新增系统消息（管理端） */
    ResultMessage addMessage(SystemMessage message);

    /** 删除消息（管理端） */
    ResultMessage deleteMessage(Integer id);

    /**  批量推送公告（管理端） */
    ResultMessage broadcastMessage(SystemMessage message);
}
