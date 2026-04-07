package com.example.core.common.mapper;

import com.example.core.common.entity.UserPro;
import com.example.core.common.vo.finance.QueryCommissionStatisticsVO;
import com.example.core.common.vo.finance.QueryUserProListVO;
import com.example.core.common.vo.finance.UserNumVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserProMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserPro record);

    int insertSelective(UserPro record);

    UserPro selectByPrimaryKey(Integer id);

    UserPro selectByUserId(@Param("userId") String userId);

    List<QueryCommissionStatisticsVO> selectListByStatistics(@Param("account") String account);

    List<UserPro> selectByUserIds(@Param("list")List<String> list);

    List<UserPro> selectByProUserId(@Param("proUserId") String proUserId);

    List<UserNumVO> selectStatisticsByUserIds(@Param("list")List<String> list);

    List<UserPro> selectByProUserIds(@Param("list")List<String> list);

    List<QueryUserProListVO> selectList(Map<String ,Object> param);

    int updateByPrimaryKeySelective(UserPro record);

    int updateByPrimaryKey(UserPro record);

    String selectProCreateMonth(@Param("userId")String userId);
}