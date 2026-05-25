package com.example.core.common.mapper;

import com.example.core.common.entity.MainInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MainInfoMapper接口，定义了主机信息相关的数据库操作方法
 */
public interface MainInfoMapper {
    /**
     * 根据主键ID删除主机信息
     * @param id 主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条主机信息记录
     * @param record 主机信息对象
     * @return 插入的记录数
     */
    int insert(MainInfo record);

    /**
     * 插入一条主机信息记录（只插入非空字段）
     * @param record 主机信息对象
     * @return 插入的记录数
     */
    int insertSelective(MainInfo record);

    /**
     * 根据主键ID查询主机信息
     * @param id 主键ID
     * @return 主机信息对象
     */
    MainInfo selectByPrimaryKey(Integer id);

    /**
     * 根据主键ID更新主机信息（只更新非空字段）
     * @param record 主机信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(MainInfo record);

    /**
     * 根据主键ID更新主机信息（更新所有字段）
     * @param record 主机信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(MainInfo record);

    /**
     * 根据主机编号查询主机信息
     * @param mainNo 主机编号
     * @return 主机信息
     */
    MainInfo selectByMainNo(@Param("mainNo") String mainNo);

    /**
     * 根据服务编号查询主机信息
     * @param serviceNo 服务编号
     * @return 主机信息
     */
    MainInfo selectByServiceNo(@Param("serviceNo") String serviceNo);

    /**
     * 根据配置ID查询主机信息列表
     * @param configId 配置ID
     * @return 主机信息列表
     */
    List<MainInfo> selectByConfigId(@Param("configId") Integer configId);
}