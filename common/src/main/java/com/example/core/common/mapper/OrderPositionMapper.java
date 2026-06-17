package com.example.core.common.mapper;

import com.example.core.common.entity.OrderPosition;
import com.example.core.common.vo.profitrecord.ProfitRecordVO;
import com.example.core.common.vo.profitrecord.ProfitTrendVO;
import com.example.core.common.vo.profitrecord.UserProductVO;
import com.example.core.common.vo.robot.HistoryPositionVO;
import com.example.core.common.vo.robot.ProfitRecordItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * OrderPositionMapper接口
 * 用于定义与订单位置相关的数据库操作方法
 */
public interface OrderPositionMapper {
    /**
     * 根据主键删除订单位置记录
     * @param id 订单位置ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条订单位置记录
     * @param record 订单位置对象
     * @return 插入的记录数
     */
    int insert(OrderPosition record);

    /**
     * 选择性插入一条订单位置记录（只插入非空字段）
     * @param record 订单位置对象
     * @return 插入的记录数
     */
    int insertSelective(OrderPosition record);

    /**
     * 根据主键查询订单位置记录
     * @param id 订单位置ID
     * @return 订单位置对象
     */
    OrderPosition selectByPrimaryKey(Integer id);

    /**
     * 选择性更新订单位置记录（只更新非空字段）
     * @param record 订单位置对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(OrderPosition record);

    /**
     * 根据主键更新订单位置记录（更新所有字段）
     * @param record 订单位置对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(OrderPosition record);


    /** 查询历史仓位列表（已结束） */
    List<HistoryPositionVO> selectHistoryPositionList(@Param("orderNo") String orderNo);

    /** 根据订单号查询仓位列表 */
    List<OrderPosition> selectByOrderNo(@Param("orderNo") String orderNo);

    /** 查询已平仓仓位用于绘制收益曲线（按结束时间升序） */
    List<OrderPosition> selectProfitCurveByOrderNo(@Param("orderNo") String orderNo);

    /** 按时间维度聚合收益（月/天） */
    List<ProfitRecordItem> selectAggregatedProfit(@Param("orderNo") String orderNo, @Param("dimension") String dimension);

    /** 查询用户收益记录列表（从策略历史仓位关联用户订单） */
    List<ProfitRecordVO> selectUserProfitRecordList(@Param("userId") String userId,
                                                     @Param("productName") String productName,
                                                     @Param("productNo") String productNo,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime") String endTime);

    /** 查询用户收益趋势数据（年/月/天维度聚合，从策略历史仓位） */
    List<ProfitTrendVO> selectUserProfitTrend(@Param("userId") String userId,
                                               @Param("dimension") String dimension,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);

    /** 查询用户的策略机器人列表（用于下拉框筛选） */
    List<UserProductVO> selectUserOrderProducts(@Param("userId") String userId);

}