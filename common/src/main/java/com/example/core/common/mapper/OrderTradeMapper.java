package com.example.core.common.mapper;

import com.example.core.common.entity.OrderTrade;

public interface OrderTradeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderTrade record);

    int insertSelective(OrderTrade record);

    OrderTrade selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderTrade record);

    int updateByPrimaryKey(OrderTrade record);
}