package com.example.core.common.mapper;


import com.example.core.common.entity.UserDiscount;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserDiscountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserDiscount record);

    int insertBatch(List<UserDiscount> list);

    int insertSelective(UserDiscount record);

    UserDiscount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserDiscount record);

    int updateByPrimaryKey(UserDiscount record);

    List<UserDiscount> selectByUserId(@Param("userId") String userId);

    List<UserDiscount> selectList(Map<String,Object> param);

//    List<UserProductNumVO> selectNumByUserIds(@Param("list")List<String> list);

    UserDiscount selectByUserIdConfig(@Param("userId") String userId,@Param("config")String config);
}