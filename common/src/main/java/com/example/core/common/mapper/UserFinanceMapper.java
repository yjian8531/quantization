package com.example.core.common.mapper;

import com.example.core.common.entity.UserFinance;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface UserFinanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserFinance record);

    int insertSelective(UserFinance record);

    UserFinance selectByPrimaryKey(Integer id);

    UserFinance selectByUserId(@Param("userId") String userId);

    int updateByPrimaryKeySelective(UserFinance record);

    int updateByPrimaryKey(UserFinance record);

    BigDecimal selectBalanceStatistics();

    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param tad 标签(add：添加余额,minus：减去余额,unbind：解冻余额,seal：冻结余额)
     * @param num
     * @return
     */
    int updateBalanceByUserId(@Param("userId")String userId, @Param("tad")String tad, @Param("num")BigDecimal num);
}