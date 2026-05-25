package com.example.core.common.mapper;


import com.example.core.common.entity.AdminLog;

import java.util.List;
import java.util.Map;

/**
 * AdminLogMapper接口 - 管理员日志数据访问接口
 * 该接口定义了对管理员日志(AdminLog)进行CRUD操作的方法
 */
public interface AdminLogMapper {
    /**
     * 根据主键删除管理员日志记录
     * @param id 日志记录的主键ID
     * @return 影响的行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条管理员日志记录
     * @param record 管理员日志实体对象
     * @return 影响的行数
     */
    int insert(AdminLog record);

    /**
     * 插入一条管理员日志记录(只插入非空字段)
     * @param record 管理员日志实体对象
     * @return 影响的行数
     */
    int insertSelective(AdminLog record);

    /**
     * 根据主键查询管理员日志记录
     * @param id 日志记录的主键ID
     * @return 管理员日志实体对象
     */
    AdminLog selectByPrimaryKey(Integer id);

    /**
     * 根据主键更新管理员日志记录(只更新非空字段)
     * @param record 管理员日志实体对象
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(AdminLog record);

    /**
     * 根据主键更新管理员日志记录(更新所有字段)
     * @param record 管理员日志实体对象
     * @return 影响的行数
     */
    int updateByPrimaryKey(AdminLog record);

    /**
     * 根据条件查询管理员日志列表
     * @param map 查询条件参数集合
     * @return 管理员日志列表
     */
    List<AdminLog> selectList(Map<String, Object> map);
}