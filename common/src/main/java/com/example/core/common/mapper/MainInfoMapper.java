package com.example.core.common.mapper;

import com.example.core.common.entity.MainInfo;

public interface MainInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MainInfo record);

    int insertSelective(MainInfo record);

    MainInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MainInfo record);

    int updateByPrimaryKey(MainInfo record);
}