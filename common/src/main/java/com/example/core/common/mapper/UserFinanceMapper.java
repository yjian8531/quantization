package com.example.core.common.mapper;

import com.example.core.common.entity.UserFinance;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 用户财务数据访问接口
 * 该接口定义了用户财务相关的数据操作方法
 */
public interface UserFinanceMapper {
    /**
     * 根据主键删除用户财务记录
     * @param id 用户财务记录的主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条用户财务记录
     * @param record 用户财务记录对象
     * @return 插入的记录数
     */
    int insert(UserFinance record);

    /**
     * 选择性插入一条用户财务记录
     * @param record 用户财务记录对象
     * @return 插入的记录数
     */
    int insertSelective(UserFinance record);

    /**
     * 根据主键查询用户财务记录
     * @param id 用户财务记录的主键ID
     * @return 用户财务记录对象
     */
    UserFinance selectByPrimaryKey(Integer id);

    /**
     * 根据用户ID查询用户财务记录
     * @param userId 用户ID
     * @return 用户财务记录对象
     */
    UserFinance selectByUserId(@Param("userId") String userId);

    /**
     * 根据主键选择性更新用户财务记录
     * @param record 用户财务记录对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(UserFinance record);

    /**
     * 根据主键更新用户财务记录
     * @param record 用户财务记录对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(UserFinance record);

    /**
     * 查询余额统计信息
     * @return 余额统计结果
     */
    BigDecimal selectBalanceStatistics();

    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param tad 标签(add：添加余额,minus：减去余额,unbind：解冻余额,seal：冻结余额)
     * @param num 金额数量
     * @return 更新的记录数
     */
    int updateBalanceByUserId(@Param("userId")String userId, @Param("tad")String tad, @Param("num")BigDecimal num);
}