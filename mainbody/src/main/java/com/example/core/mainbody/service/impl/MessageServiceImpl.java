package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.SystemMessage;
import com.example.core.common.mapper.SystemMessageMapper;
import com.example.core.common.utils.ResultMessage;

import com.example.core.mainbody.service.MessageService;
import com.example.core.mainbody.so.user.QueryMessageSO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private SystemMessageMapper systemMessageMapper;


    /**
     * 查询未读消息数量
     */
    @Override
    public ResultMessage queryUnreadCount(String userId) {
        int count = systemMessageMapper.countUnread(userId);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, count);
    }

    /**
     * 查询消息列表
     */
    @Override
    public ResultMessage queryMessageList(String userId, QueryMessageSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<SystemMessage> page = (Page<SystemMessage>) systemMessageMapper.selectMessageList(userId, so.getType());
        List<SystemMessage> voList = new ArrayList<>(page.getResult());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", voList);
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 标记为已读
     */
    @Override
    public ResultMessage markAsRead(String userId, Integer messageId) {
        int result = systemMessageMapper.markAsRead(messageId, userId);
        if(result > 0){
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "成功");
        }else {
            return new ResultMessage(ResultMessage.FAILED_CODE, "失败");
        }
    }
}
