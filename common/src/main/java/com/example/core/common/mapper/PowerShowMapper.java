package com.example.core.common.mapper;


import com.example.core.common.entity.PowerShow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PowerShowMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PowerShow record);

    int insertSelective(PowerShow record);

    PowerShow selectByPrimaryKey(Integer id);

    List<PowerShow> selectByIds(@Param("list") List<Integer> list);

    int updateByPrimaryKeySelective(PowerShow record);

    int updateByPrimaryKey(PowerShow record);
}