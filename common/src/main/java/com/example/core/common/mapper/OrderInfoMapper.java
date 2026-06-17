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


    /** 查询用户机器人总数 */
    int selectUserRobotCount(@Param("userId") String userId, @Param("exchange") Integer exchange);

    /** 查询用户机器人列表（分页+排序） */
    List<RobotListVO> selectUserRobotList(@Param("userId") String userId, @Param("exchange") Integer exchange, @Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("sortType") String sortType);


    /** 查询机器人详情（包含当前仓位） */
    RobotDetailVO selectRobotDetail(@Param("orderNo") String orderNo, @Param("userId") String userId);

    /** 根据订单号查询 */
    OrderInfo selectByOrderNo(@Param("orderNo") String orderNo);

    /** 查询公开机器人总数 */
    int selectPublicRobotCount(@Param("exchange") Integer exchange);

    /** 查询公开机器人列表（分页+排序） */
    List<RobotListVO> selectPublicRobotList(@Param("exchange") Integer exchange, @Param("start") Integer start, @Param("pageSize") Integer pageSize, @Param("sortType") String sortType);

    /** 查询公开机器人详情 */
    com.example.core.common.vo.robot.PublicRobotDetailVO selectPublicRobotDetail(@Param("orderNo") String orderNo);

    /** 平台汇总 - 活跃用户数（拥有运行中或暂停订单的独立用户数） */
    int selectActiveUserCount();

    /** 平台汇总 - 年化率区间（最低年化、最高年化） */
    java.util.Map<String, Object> selectAnnualizedRateRange();

    /** 查询用户所有策略机器人的累计收益 */
    java.math.BigDecimal selectTotalIncomeByUserId(@Param("userId") String userId);

    /** 查询所有非结束状态的订单（用于过期续费监控） */
    List<OrderInfo> selectActiveList();

}