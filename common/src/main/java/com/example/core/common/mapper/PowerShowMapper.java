package com.example.core.common.mapper;


import com.example.core.common.entity.PowerShow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PowerShowMapper接口 - 数据访问层接口
 * 该接口定义了对PowerShow实体进行基本CRUD操作的方法
 */
public interface PowerShowMapper {
    /**
     * 根据主键删除记录
     * @param id 记录的主键ID
     * @return 影响的行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条完整的PowerShow记录
     * @param record 要插入的PowerShow对象
     * @return 影响的行数
     */
    int insert(PowerShow record);

    /**
     * 插入一条PowerShow记录，只插入非空字段
     * @param record 要插入的PowerShow对象
     * @return 影响的行数
     */
    int insertSelective(PowerShow record);

    /**
     * 根据主键查询记录
     * @param id 记录的主键ID
     * @return 查询到的PowerShow对象
     */
    PowerShow selectByPrimaryKey(Integer id);

    /**
     * 根据ID列表批量查询记录
     * @param list 包含多个ID的列表
     * @return 查询到的PowerShow对象列表
     */
    List<PowerShow> selectByIds(@Param("list") List<Integer> list);

    /**
     * 更新PowerShow记录，只更新非空字段
     * @param record 要更新的PowerShow对象
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(PowerShow record);

    /**
     * 根据主键更新PowerShow记录的所有字段
     * @param record 要更新的PowerShow对象
     * @return 影响的行数
     */
    int updateByPrimaryKey(PowerShow record);
}