package com.example.core.common.mapper;

import com.example.core.common.entity.OrderTrade;
import com.example.core.common.vo.robot.TradeRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderTradeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderTrade record);

    int insertSelective(OrderTrade record);

    OrderTrade selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderTrade record);

    int updateByPrimaryKey(OrderTrade record);


    /** 查询交易记录列表 */
    List<TradeRecordVO> selectTradeRecordList(@Param("orderNo") String orderNo);

    /** 根据订单号查询交易列表 */
    List<OrderTrade> selectByOrderNo(@Param("orderNo") String orderNo);

    /** 平台汇总 - 交易总额 */
    java.math.BigDecimal selectTradeTotalAmount();

    /** 平台汇总 - 交易次数 */
    int selectTradeTotalCount();

}