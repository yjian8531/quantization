package com.example.core.common.mapper;

import com.example.core.common.entity.MainConfig;

public interface MainConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MainConfig record);

    int insertSelective(MainConfig record);

    MainConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MainConfig record);

    int updateByPrimaryKey(MainConfig record);
}