package com.example.core.common.mapper;


import com.example.core.common.entity.SysOperation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysOperationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysOperation record);

    int insertSelective(SysOperation record);

    SysOperation selectByPrimaryKey(Integer id);

    SysOperation selectByTail(@Param("tail") String tail);

    List<SysOperation> selectByIds(@Param("list") List<Integer> list);

    int updateByPrimaryKeySelective(SysOperation record);

    int updateByPrimaryKey(SysOperation record);
}