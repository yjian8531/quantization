package com.example.core.common.mapper;

import com.example.core.common.entity.OrderTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderTaskMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderTask record);

    int insertSelective(OrderTask record);

    OrderTask selectByPrimaryKey(Integer id);

    List<OrderTask> selectByPending();

    int updateByPrimaryKeySelective(OrderTask record);

    int updateByPrimaryKey(OrderTask record);
}