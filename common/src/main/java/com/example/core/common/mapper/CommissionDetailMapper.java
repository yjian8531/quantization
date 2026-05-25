package com.example.core.common.mapper;

import com.example.core.common.entity.CommissionDetail;
import com.example.core.common.so.finance.QueryCommissionListSO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommissionDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommissionDetail record);

    int insertSelective(CommissionDetail record);

    CommissionDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommissionDetail record);

    int updateByPrimaryKey(CommissionDetail record);

    /**
     * 查询奖励列表
     * @param userId
     * @param type
     * @return
     */
    List<CommissionDetail> selectRewardList(@Param("userId") String userId, @Param("type") Integer type);



    // 查询管理员佣金列表
    List<CommissionDetail> selectAdminCommissionList(QueryCommissionListSO so);

    // 查询用户佣金列表
    List<CommissionDetail> selectByUserId(String userId);

//    /**
//     * 查询团队总消费
//     * @param userId
//     * @return
//     */
//    BigDecimal sumTeamConsumption(@Param("userId") String userId);
//    /**
//     * 团队总奖励
//     * @param userId
//     * @return
//     */
//    BigDecimal sumTeamCommission(@Param("userId") String userId);
//    /**
//     * 团队总消费
//     * @param userId
//     * @param startTime
//     * @return
//     */
//    BigDecimal sumMonthlyConsumption(@Param("userId") String userId, @Param("startTime") String startTime);
//    /**
//     * 团队总奖励
//     * @param userId
//     * @param startTime
//     * @return
//     */
//    BigDecimal sumMonthlyCommission(@Param("userId") String userId, @Param("startTime") String startTime);

}