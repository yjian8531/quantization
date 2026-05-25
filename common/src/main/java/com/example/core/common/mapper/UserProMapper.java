package com.example.core.common.mapper;

import com.example.core.common.entity.UserPro;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户推广数据访问接口
 * 定义了用户推广相关的数据库操作方法
 */
public interface UserProMapper {
    /**
     * 根据主键删除记录
     * @param id 主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条完整记录
     * @param record 用户推广信息对象
     * @return 插入的记录数
     */
    int insert(UserPro record);

    /**
     * 插入一条记录（只插入非空字段）
     * @param record 用户推广信息对象
     * @return 插入的记录数
     */
    int insertSelective(UserPro record);

    /**
     * 根据主键查询记录
     * @param id 主键ID
     * @return 用户推广信息对象
     */
    UserPro selectByPrimaryKey(Integer id);

    /**
     * 根据主键更新记录（只更新非空字段）
     * @param record 用户推广信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(UserPro record);

    /**
     * 根据主键更新记录（更新所有字段）
     * @param record 用户推广信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(UserPro record);
    /** 根据推广人 ID 查询所有下级 */
    List<UserPro> selectByProUserId(@Param("proUserId") String proUserId);

//    /** 统计当月新增下级人数 */
//    int countMonthlyNewUsers(@Param("proUserId") String proUserId, @Param("startTime") String startTime);
//
//    /** 统计该推广人的总下级人数 */
//    int countTotalSubUsers(@Param("proUserId") String proUserId);

    /** 查询推广用户列表（关联用户信息） */
    List<Map<String, Object>> selectPromotionUsers(@Param("proUserId") String proUserId);

    UserPro selectByUserId(String userId);
}