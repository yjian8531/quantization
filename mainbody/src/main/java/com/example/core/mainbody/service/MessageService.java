package com.example.core.mainbody.service;

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
}
