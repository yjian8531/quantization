package com.example.core.common.mapper;

import com.example.core.common.entity.SymbolInfo;

public interface SymbolInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SymbolInfo record);

    int insertSelective(SymbolInfo record);

    SymbolInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SymbolInfo record);

    int updateByPrimaryKey(SymbolInfo record);
}