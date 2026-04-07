package com.example.core.common.mapper;


import com.example.core.common.entity.AdminInfo;

import java.util.List;

public interface AdminInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AdminInfo record);

    int insertSelective(AdminInfo record);

    AdminInfo selectByPrimaryKey(Integer id);

    AdminInfo selectByAccount(String account);

    AdminInfo selectById(String adminId);

    AdminInfo selectDeriveById(Integer adminId);

    List<AdminInfo> selecctChatList();

    int updateByPrimaryKeySelective(AdminInfo record);

    int updateByPrimaryKey(AdminInfo record);
}