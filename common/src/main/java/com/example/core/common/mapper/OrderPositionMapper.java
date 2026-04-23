package com.example.core.common.mapper;

import com.example.core.common.entity.OrderPosition;

public interface OrderPositionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderPosition record);

    int insertSelective(OrderPosition record);

    OrderPosition selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderPosition record);

    int updateByPrimaryKey(OrderPosition record);
}