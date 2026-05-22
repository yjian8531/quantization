package com.example.core.common.mapper;

import com.example.core.common.entity.SystemMessage;
import com.example.core.common.so.user.QueryMessageAdminSO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SystemMessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SystemMessage record);

    int insertSelective(SystemMessage record);

    SystemMessage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SystemMessage record);

    int updateByPrimaryKey(SystemMessage record);

    /** 查询消息列表（支持分页和类型筛选） */
    List<SystemMessage> selectMessageList(@Param("userId") String userId, @Param("type") Integer type);

    /** 统计未读消息数 */
    int countUnread(@Param("userId") String userId);

    /** 标记消息已读 */
    int markAsRead(@Param("id") Integer id, @Param("userId") String userId);


    /** 管理端分页查询消息列表 */
    List<SystemMessage> selectAdminMessageList(QueryMessageAdminSO so);
}