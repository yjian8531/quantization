package com.example.core.common.mapper;


import com.example.core.common.entity.SysOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统操作数据访问接口
 * 该接口定义了系统操作相关的数据库操作方法
 */
public interface SysOperationMapper {
    /**
     * 根据主键删除系统操作记录
     * @param id 系统操作的主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条系统操作记录
     * @param record 系统操作实体对象
     * @return 插入的记录数
     */
    int insert(SysOperation record);

    /**
     * 选择性插入系统操作记录
     * 只插入非空字段
     * @param record 系统操作实体对象
     * @return 插入的记录数
     */
    int insertSelective(SysOperation record);

    /**
     * 根据主键查询系统操作记录
     * @param id 系统操作的主键ID
     * @return 系统操作实体对象
     */
    SysOperation selectByPrimaryKey(Integer id);

    /**
     * 根据操作尾部标识查询系统操作记录
     * @param tail 操作尾部标识
     * @return 系统操作实体对象
     */
    SysOperation selectByTail(@Param("tail") String tail);

    /**
     * 根据ID列表查询系统操作记录
     * @param list 系统操作ID列表
     * @return 系统操作实体对象列表
     */
    List<SysOperation> selectByIds(@Param("list") List<Integer> list);

    /**
     * 选择性更新系统操作记录
     * 只更新非空字段
     * @param record 系统操作实体对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(SysOperation record);

    /**
     * 根据主键更新系统操作记录
     * 更新所有字段
     * @param record 系统操作实体对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(SysOperation record);
}