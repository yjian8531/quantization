package com.example.core.common.mapper;

import com.example.core.common.entity.DepositOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DepositOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DepositOrder record);

    int insertSelective(DepositOrder record);

    DepositOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DepositOrder record);

    int updateByPrimaryKey(DepositOrder record);

    /**
     * 根据订单号查询充值订单
     * @param orderNo
     * @return
     */
    DepositOrder selectByOrderNo(@Param("orderNo") String orderNo);


    /** 根据地址和金额查找待确认订单（回调匹配用） */
    DepositOrder selectPendingByAddressAndAmount(@Param("address") String address, @Param("amount") BigDecimal amount);

    /** 查询用户充值订单列表 */
    List<DepositOrder> selectByUserId(@Param("userId") String userId);
}