package com.example.core.common.mapper;


import com.example.core.common.entity.UserWx;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserWxMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWx record);

    int insertSelective(UserWx record);

    UserWx selectByPrimaryKey(Integer id);

    UserWx selectByUserId(@Param("userId")String userId);

    List<UserWx> selectByOpenId(@Param("openId")String openId);

    UserWx selectByUserAndOpen(@Param("userId") String userId, @Param("openId")String openId);

    int updateByPrimaryKeySelective(UserWx record);

    int updateByPrimaryKey(UserWx record);
}