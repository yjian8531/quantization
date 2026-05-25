package com.example.core.common.mapper;

import com.example.core.common.entity.SymbolInfo;
import com.example.core.common.vo.product.SymbolListVO;

import java.util.List;

/**
 * SymbolInfoMapper接口 - 数据访问层接口
 * 该接口定义了对SymbolInfo对象进行数据库操作的方法
 */
public interface SymbolInfoMapper {
    /**
     * 根据主键删除记录
     * @param id 主键ID
     * @return 影响的行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条完整的SymbolInfo记录
     * @param record 要插入的SymbolInfo对象
     * @return 影响的行数
     */
    int insert(SymbolInfo record);

    /**
     * 插入一条SymbolInfo记录，只插入非空字段
     * @param record 要插入的SymbolInfo对象
     * @return 影响的行数
     */
    int insertSelective(SymbolInfo record);

    /**
     * 根据主键查询SymbolInfo记录
     * @param id 主键ID
     * @return 查询到的SymbolInfo对象
     */
    SymbolInfo selectByPrimaryKey(Integer id);

    /**
     * 根据主键更新SymbolInfo记录，只更新非空字段
     * @param record 要更新的SymbolInfo对象
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(SymbolInfo record);

    /**
     * 根据主键更新SymbolInfo记录，更新所有字段
     * @param record 要更新的SymbolInfo对象
     * @return 影响的行数
     */
    int updateByPrimaryKey(SymbolInfo record);

    /** 查询可用币对列表 */
    List<SymbolListVO> selectSymbolList();

}