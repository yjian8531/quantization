package com.example.core.common.mapper;


import com.example.core.common.entity.AdminLog;

import java.util.List;
import java.util.Map;

public interface AdminLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminLog record);

    int insertSelective(AdminLog record);

    AdminLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AdminLog record);

    int updateByPrimaryKey(AdminLog record);

    List<AdminLog> selectList(Map<String, Object> map);
}