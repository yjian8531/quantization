package com.example.core.common.mapper;

import com.example.core.common.entity.OrderPosition;
import com.example.core.common.vo.robot.HistoryPositionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderPositionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderPosition record);

    int insertSelective(OrderPosition record);

    OrderPosition selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderPosition record);

    int updateByPrimaryKey(OrderPosition record);


    /** 查询历史仓位列表（已结束） */
    List<HistoryPositionVO> selectHistoryPositionList(@Param("orderNo") String orderNo);

    /** 根据订单号查询仓位列表 */
    List<OrderPosition> selectByOrderNo(@Param("orderNo") String orderNo);

}