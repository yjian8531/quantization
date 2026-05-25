package com.example.core.common.mapper;

import com.example.core.common.entity.MainConfig;

/**
 * 主机配置映射接口，用于定义与主机配置相关的数据库操作方法
 */
public interface MainConfigMapper {
    /**
     * 根据主键ID删除主机配置记录
     * @param id 主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条完整的主机配置记录
     * @param record 主机配置对象
     * @return 插入的记录数
     */
    int insert(MainConfig record);

    /**
     * 插入一条主机配置记录（只插入非空字段）
     * @param record 主机配置对象
     * @return 插入的记录数
     */
    int insertSelective(MainConfig record);

    /**
     * 根据主键ID查询主机配置记录
     * @param id 主键ID
     * @return 主机配置对象
     */
    MainConfig selectByPrimaryKey(Integer id);

    /**
     * 根据主键ID更新主机配置记录（只更新非空字段）
     * @param record 主机配置对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(MainConfig record);

    /**
     * 根据主键ID更新主机配置记录（更新所有字段）
     * @param record 主机配置对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(MainConfig record);

    /**
     * 查询状态正常的主机配置（取第一条）
     * @return 主机配置
     */
    MainConfig selectNormalConfig();
}