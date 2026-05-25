package com.example.core.common.mapper;

import com.example.core.common.entity.SystemMessage;
import com.example.core.common.so.user.QueryMessageAdminSO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统消息数据访问接口
 * 该接口定义了系统消息的增删改查等基本操作
 */
public interface SystemMessageMapper {
    /**
     * 根据主键删除系统消息
     * @param id 系统消息ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条系统消息记录
     * @param record 系统消息实体对象
     * @return 插入的记录数
     */
    int insert(SystemMessage record);

    /**
     * 插入一条系统消息记录（只插入非空字段）
     * @param record 系统消息实体对象
     * @return 插入的记录数
     */
    int insertSelective(SystemMessage record);

    /**
     * 根据主键查询系统消息
     * @param id 系统消息ID
     * @return 系统消息实体对象
     */
    SystemMessage selectByPrimaryKey(Integer id);

    /**
     * 根据主键更新系统消息（只更新非空字段）
     * @param record 系统消息实体对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(SystemMessage record);

    /**
     * 根据主键更新系统消息（更新所有字段）
     * @param record 系统消息实体对象
     * @return 更新的记录数
     */
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