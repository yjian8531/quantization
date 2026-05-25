package com.example.core.common.mapper;

import com.example.core.common.entity.UserLogin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户登录数据访问接口
 * 该接口定义了用户登录相关的数据库操作方法
 */
public interface UserLoginMapper {
    /**
     * 根据主键删除用户登录记录
     * @param id 用户登录记录的主键ID
     * @return 影响的行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条用户登录记录
     * @param record 用户登录记录对象
     * @return 影响的行数
     */
    int insert(UserLogin record);

    /**
     * 选择性插入一条用户登录记录
     * 只插入非空字段的值
     * @param record 用户登录记录对象
     * @return 影响的行数
     */
    int insertSelective(UserLogin record);

    /**
     * 根据主键查询用户登录记录
     * @param id 用户登录记录的主键ID
     * @return 用户登录记录对象
     */
    UserLogin selectByPrimaryKey(Integer id);

    /**
     * 根据用户ID查询用户登录记录列表
     * @param userId 用户ID
     * @return 用户登录记录列表
     */
    List<UserLogin> selectList(@Param("userId") String userId);

    /**
     * 选择性更新用户登录记录
     * 只更新非空字段的值
     * @param record 用户登录记录对象
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(UserLogin record);

    /**
     * 根据主键更新用户登录记录
     * 更新所有字段的值
     * @param record 用户登录记录对象
     * @return 影响的行数
     */
    int updateByPrimaryKey(UserLogin record);
}