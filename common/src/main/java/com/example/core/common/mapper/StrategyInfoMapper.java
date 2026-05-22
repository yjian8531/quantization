package com.example.core.common.mapper;

import com.example.core.common.entity.StrategyInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StrategyInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StrategyInfo record);

    int insertSelective(StrategyInfo record);

    StrategyInfo selectByPrimaryKey(Integer id);

    /** 根据策略模板ID查询 */
    StrategyInfo selectByStrategyId(@Param("strategyId") String strategyId);

    /** 查询所有策略模板列表 */
    List<StrategyInfo> selectAll();

    int updateByPrimaryKeySelective(StrategyInfo record);

    int updateByPrimaryKeyWithBLOBs(StrategyInfo record);

    int updateByPrimaryKey(StrategyInfo record);
}