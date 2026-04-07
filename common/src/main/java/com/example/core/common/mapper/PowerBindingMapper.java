package com.example.core.common.mapper;

import com.example.core.common.entity.PowerBinding;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PowerBindingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PowerBinding record);

    int insertSelective(PowerBinding record);

    PowerBinding selectByPrimaryKey(Integer id);

    /**
     * 查询管理员的分组权限
     * @param adminId
     * @return
     */
    List<PowerBinding> selectGroupingByAdmin(@Param("adminId") Integer adminId);

    /**
     * 根据分组查询权限信息
     * @param list 分组ID集合
     * @param type 类型(1:分组展示权限,2:分组操作权限)
     * @return
     */
    List<PowerBinding> selectPowerByGrouping(@Param("list") List<Integer> list,@Param("type") Integer type);

    int updateByPrimaryKeySelective(PowerBinding record);

    int updateByPrimaryKey(PowerBinding record);
}