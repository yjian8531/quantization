package com.example.core.common.mapper;

import com.example.core.common.entity.UserLogin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserLoginMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserLogin record);

    int insertSelective(UserLogin record);

    UserLogin selectByPrimaryKey(Integer id);

    List<UserLogin> selectList(@Param("userId") String userId);

    int updateByPrimaryKeySelective(UserLogin record);

    int updateByPrimaryKey(UserLogin record);
}