package com.example.core.common.mapper;

import com.example.core.common.entity.UserPro;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserProMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPro record);

    int insertSelective(UserPro record);

    UserPro selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserPro record);

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