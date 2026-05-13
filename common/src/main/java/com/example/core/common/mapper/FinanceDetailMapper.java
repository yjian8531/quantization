package com.example.core.common.mapper;

import com.example.core.common.entity.FinanceDetail;
import com.example.core.common.vo.finance.BillListVO;
import com.example.core.common.vo.finance.RechargeRecordVO;
import com.example.core.common.so.finance.QueryBillListAdminSO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户财务明细Mapper
 */
@Mapper
public interface FinanceDetailMapper {

    // 根据主键ID查询单条账单记录
    FinanceDetail selectByPrimaryKey(Integer id);

    // 插入完整账单记录（所有字段都要有值）
    int insert(FinanceDetail record);

    // 选择性插入（只插入非空字段）
    int insertSelective(FinanceDetail record);

    // 根据主键更新（只更新非空字段）
    int updateByPrimaryKeySelective(FinanceDetail record);

    // 根据主键删除记录
    int deleteByPrimaryKey(Integer id);

    // 查询某个用户的所有账单记录
    List<FinanceDetail> selectByUserId(@Param("userId") String userId);

    // 分页查询账单（按tag和type筛选）
    List<FinanceDetail> selectPageByUserId(@Param("userId") String userId,
                                           @Param("tag") String tag,
                                           @Param("type") Integer type);

    // 查询用户累计收益（只算佣金+续费）
    BigDecimal selectTotalProfit(@Param("userId") String userId);

    // 查询用户累计总收入（所有收入）
    BigDecimal selectTotalIncome(@Param("userId") String userId);

    // 查询用户累计总支出（所有支出）
    BigDecimal selectTotalExpense(@Param("userId") String userId);

    // 查询用户本周总收入
    BigDecimal selectWeekIncome(@Param("userId") String userId,
                                @Param("startTime") String startTime,
                                @Param("endTime") String endTime);

    // 查询用户本周总支出
    BigDecimal selectWeekExpense(@Param("userId") String userId,
                                 @Param("startTime") String startTime,
                                 @Param("endTime") String endTime);

    // 查询用户本周分润（佣金+续费）
    BigDecimal selectWeekCommission(@Param("userId") String userId,
                                    @Param("startTime") String startTime,
                                    @Param("endTime") String endTime);

    // 分页查询账单列表（支持tags多标签筛选、时间范围）可以删除
    List<FinanceDetail> selectBillListByUserId(@Param("userId") String userId,
                                               @Param("direction") Integer direction,
                                               @Param("tags") List<String> tags,
                                               @Param("startTime") String startTime,
                                               @Param("endTime") String endTime);

    // 查询用户账单总数
    Integer selectBillCountByUserId(@Param("userId") String userId);


    // 充值记录 可以删除
    List<FinanceDetail> selectRechargeList(String userId);

    // 充值总额
    BigDecimal selectTotalRecharge(String userId);

    // 充值次数
    Integer selectRechargeCount(String userId);


    // 查询账单列表（支持tags多标签筛选、时间范围）
    List<BillListVO> selectBillListByUserIdWithConvert(@Param("userId") String userId,
                                                       @Param("direction") Integer direction,
                                                       @Param("tags") List<String> tags,
                                                       @Param("startTime") String startTime,
                                                       @Param("endTime") String endTime);
    /**
     * 查询充值记录列表（包含类型转换和筛选）
     */
    List<RechargeRecordVO> selectRechargeListWithConvert(@Param("userId") String userId,
                                                         @Param("coinType")String coinType,
                                                         @Param("chainType") String chainType,
                                                         @Param("startTime") String startTime,
                                                         @Param("endTime") String endTime);

    // 后台查询账单列表
    List<FinanceDetail> selectAdminBillList(QueryBillListAdminSO so);

}
