package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.SystemMessage;
import com.example.core.common.mapper.SystemMessageMapper;
import com.example.core.common.so.user.QueryMessageAdminSO;
import com.example.core.common.utils.RedisUtil;
import com.example.core.common.utils.ResultMessage;

import com.example.core.common.utils.StringUtils;
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
        List<SystemMessage> allMessages = systemMessageMapper.selectMessageList(userId, null);
        int unreadCount = 0;
        for(SystemMessage msg : allMessages){
            if(StringUtils.isNotEmpty(msg.getUserId())){
                // 私人消息：看数据库
                if(msg.getIsRead() == 0) unreadCount++;
            }else{
                // 全局公告：看Redis
                String key = "MESSAGE:READ:" + msg.getId();
                if(!RedisUtil.sismember(key, userId)){
                    unreadCount++;
                }
            }
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, unreadCount);
    }

    /**
     * 查询消息列表
     */
    @Override
    public ResultMessage queryMessageList(String userId, QueryMessageSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<SystemMessage> page = (Page<SystemMessage>) systemMessageMapper.selectMessageList(userId, so.getType());
        List<SystemMessage> voList = new ArrayList<>(page.getResult());
        for(SystemMessage msg : voList){
            if(StringUtils.isEmpty(msg.getUserId())){
                // 全局公告：从Redis获取状态
                String key = "MESSAGE:READ:" + msg.getId();
                msg.setIsRead(RedisUtil.sismember(key, userId) ? 1 : 0);
            }
            // 私人消息：保持数据库原有的is_read值
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", voList);
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 标记消息已读
     */
    @Override
    public ResultMessage markAsRead(String userId, Integer messageId) {
        SystemMessage msg = systemMessageMapper.selectByPrimaryKey(messageId);
        if(msg == null){
            return new ResultMessage(ResultMessage.FAILED_CODE, "消息不存在");
        }

        if(StringUtils.isNotEmpty(msg.getUserId())){
            // 私人消息：更新数据库
            msg.setIsRead(1);
            msg.setReadTime(new Date());
            systemMessageMapper.updateByPrimaryKeySelective(msg);
        }else{
            // 全局公告：写入Redis
            String key = "MESSAGE:READ:" + messageId;
            RedisUtil.sadd(key, userId);
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "该消息已读");
    }


    /**
     * 查询消息列表（管理端）
     */
    @Override
    public ResultMessage queryMessageListForAdmin(QueryMessageAdminSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<SystemMessage> page = (Page<SystemMessage>) systemMessageMapper.selectAdminMessageList(so);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 新增系统消息（管理端）
     */
    @Override
    public ResultMessage addMessage(SystemMessage message) {
        message.setCreateTime(new Date());
        message.setIsRead(0);
        systemMessageMapper.insertSelective(message);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "发送成功");
    }

    /**
     * 删除消息（管理端）
     */
    @Override
    public ResultMessage deleteMessage(Integer id) {
        systemMessageMapper.deleteByPrimaryKey(id);
        RedisUtil.del("MESSAGE:READ:" + id);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
    }

    /**
     * 批量推送公告（管理端）
     * userId 为空表示全员公告
     */
    @Override
    public ResultMessage broadcastMessage(SystemMessage message) {
        message.setUserId(null);
        message.setIsRead(0);
        message.setCreateTime(new Date());
        systemMessageMapper.insertSelective(message);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "全员公告推送成功");
    }
}
