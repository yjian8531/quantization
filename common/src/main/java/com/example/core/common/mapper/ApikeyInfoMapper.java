package com.example.core.common.mapper;

import com.example.core.common.entity.ApikeyInfo;

public interface ApikeyInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApikeyInfo record);

    int insertSelective(ApikeyInfo record);

    ApikeyInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApikeyInfo record);

    int updateByPrimaryKey(ApikeyInfo record);
}