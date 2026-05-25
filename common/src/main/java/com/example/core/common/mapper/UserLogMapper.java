package com.example.core.common.mapper;

import com.example.core.common.entity.UserLog;

import java.util.List;
import java.util.Map;

/**
 * UserLogMapper接口 - 用户日志数据访问层接口
 * 该接口定义了用户日志相关的数据库操作方法，包括增删改查等功能
 */
public interface UserLogMapper {
    /**
     * 根据主键删除用户日志记录
     * @param id 用户日志的主键ID
     * @return 影响的行数，删除成功返回1，失败返回0
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条完整的用户日志记录
     * @param record 包含完整用户日志信息的UserLog对象
     * @return 影响的行数，插入成功返回1，失败返回0
     */
    int insert(UserLog record);

    /**
     * 插入一条用户日志记录（只插入非空字段）
     * @param record 包含用户日志信息的UserLog对象，只会插入非空字段
     * @return 影响的行数，插入成功返回1，失败返回0
     */
    int insertSelective(UserLog record);

    /**
     * 根据主键查询用户日志记录
     * @param id 用户日志的主键ID
     * @return 匹配的用户日志记录，如果没有找到则返回null
     */
    UserLog selectByPrimaryKey(Integer id);

    /**
     * 根据条件查询用户日志列表
     * @param param 包含查询条件的Map集合，键为字段名，值为对应的查询值
     * @return 符合查询条件的用户日志列表，如果没有匹配记录则返回空列表
     */
    List<UserLog> selectList(Map<String,Object> param);

    /**
     * 根据主键更新用户日志记录（只更新非空字段）
     * @param record 包含用户日志信息的UserLog对象，只会更新非空字段
     * @return 影响的行数，更新成功返回1，失败返回0
     */
    int updateByPrimaryKeySelective(UserLog record);

    /**
     * 根据主键更新用户日志记录（更新所有字段）
     * @param record 包含完整用户日志信息的UserLog对象，会更新所有字段
     * @return 影响的行数，更新成功返回1，失败返回0
     */
    int updateByPrimaryKey(UserLog record);
}