package com.example.core.common.mapper;


import com.example.core.common.entity.AdminInfo;

import java.util.List;

/**
 * 管理员信息数据访问接口
 * 该接口定义了对管理员信息进行增删改查操作的方法
 */
public interface AdminInfoMapper {
    /**
     * 根据主键删除管理员信息
     * @param id 管理员ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入管理员信息
     * @param record 管理员信息对象
     * @return 插入的记录数
     */
    int insert(AdminInfo record);

    /**
     * 选择性插入管理员信息
     * 只插入非空字段
     * @param record 管理员信息对象
     * @return 插入的记录数
     */
    int insertSelective(AdminInfo record);

    /**
     * 根据主键查询管理员信息
     * @param id 管理员ID
     * @return 管理员信息对象
     */
    AdminInfo selectByPrimaryKey(Integer id);

    /**
     * 根据账号查询管理员信息
     * @param account 管理员账号
     * @return 管理员信息对象
     */
    AdminInfo selectByAccount(String account);

    /**
     * 根据管理员ID查询管理员信息
     * @param adminId 管理员ID
     * @return 管理员信息对象
     */
    AdminInfo selectById(String adminId);

    /**
     * 根据管理员ID派生查询管理员信息
     * @param adminId 管理员ID
     * @return 管理员信息对象
     */
    AdminInfo selectDeriveById(Integer adminId);

    /**
     * 查询聊天列表
     * @return 管理员信息列表
     */
    List<AdminInfo> selecctChatList();

    /**
     * 选择性更新管理员信息
     * 只更新非空字段
     * @param record 管理员信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(AdminInfo record);

    /**
     * 根据主键更新管理员信息
     * @param record 管理员信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(AdminInfo record);
}