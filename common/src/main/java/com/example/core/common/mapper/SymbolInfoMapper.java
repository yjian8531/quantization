package com.example.core.common.mapper;

import com.example.core.common.entity.SymbolInfo;
import com.example.core.common.vo.product.SymbolListVO;

import java.util.List;

public interface SymbolInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SymbolInfo record);

    int insertSelective(SymbolInfo record);

    SymbolInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SymbolInfo record);

    int updateByPrimaryKey(SymbolInfo record);

    /** 查询可用币对列表 */
    List<SymbolListVO> selectSymbolList();

}