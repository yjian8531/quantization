package com.example.core.common.mapper;

import com.example.core.common.entity.OrderInfo;
import com.example.core.common.vo.robot.RobotDetailVO;
import com.example.core.common.vo.robot.RobotListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    OrderInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);


    /** 查询用户机器人列表 */
    List<RobotListVO> selectUserRobotList(@Param("userId") String userId, @Param("exchange") Integer exchange);


    /** 查询机器人详情（包含当前仓位） */
    RobotDetailVO selectRobotDetail(@Param("orderId") Integer orderId, @Param("userId") String userId);

    /** 根据订单号查询 */
    OrderInfo selectByOrderNo(@Param("orderNo") String orderNo);

    /** 根据策略ID查询 */
    OrderInfo selectByStrategyId(@Param("strategyId") String strategyId);

}