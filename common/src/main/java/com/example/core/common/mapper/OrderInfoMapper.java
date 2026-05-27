package com.example.core.common.mapper;

import com.example.core.common.entity.OrderInfo;
import com.example.core.common.vo.robot.RobotDetailVO;
import com.example.core.common.vo.robot.RobotListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OrderInfoMapper接口
 * 定义了订单信息相关的数据库操作方法
 */
public interface OrderInfoMapper {
    /**
     * 根据主键删除订单信息
     * @param id 订单ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入订单信息
     * @param record 订单信息对象
     * @return 插入的记录数
     */
    int insert(OrderInfo record);

    /**
     * 选择性插入订单信息（只插入非空字段）
     * @param record 订单信息对象
     * @return 插入的记录数
     */
    int insertSelective(OrderInfo record);

    /**
     * 根据主键查询订单信息
     * @param id 订单ID
     * @return 订单信息对象
     */
    OrderInfo selectByPrimaryKey(Integer id);

    /**
     * 选择性更新订单信息（只更新非空字段）
     * @param record 订单信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(OrderInfo record);

    /**
     * 根据主键更新订单信息（更新所有字段）
     * @param record 订单信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(OrderInfo record);


    /** 查询用户机器人列表 */
    List<RobotListVO> selectUserRobotList(@Param("userId") String userId, @Param("exchange") Integer exchange);


    /** 查询机器人详情（包含当前仓位） */
    RobotDetailVO selectRobotDetail(@Param("orderId") Integer orderId, @Param("userId") String userId);

    /** 根据订单号查询 */
    OrderInfo selectByOrderNo(@Param("orderNo") String orderNo);

    /** 查询公开机器人列表 */
    List<RobotListVO> selectPublicRobotList(@Param("exchange") Integer exchange);

}