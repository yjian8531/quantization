package com.example.core.common.mapper;

import com.example.core.common.entity.StrategyInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 策略信息数据访问接口
 * 该接口定义了对策略信息（StrategyInfo）进行数据库操作的各类方法
 */
public interface StrategyInfoMapper {
    /**
     * 根据主键删除策略信息
     * @param id 策略信息的主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条策略信息记录
     * @param record 要插入的策略信息对象
     * @return 插入的记录数
     */
    int insert(StrategyInfo record);

    /**
     * 选择性插入策略信息记录
     * 只插入非空字段的值
     * @param record 要插入的策略信息对象
     * @return 插入的记录数
     */
    int insertSelective(StrategyInfo record);

    /**
     * 根据主键查询策略信息
     * @param id 策略信息的主键ID
     * @return 查询到的策略信息对象
     */
    StrategyInfo selectByPrimaryKey(Integer id);

    /** 根据策略模板ID查询 */
    StrategyInfo selectByStrategyId(@Param("strategyId") String strategyId);

    /** 查询所有策略模板列表 */
    List<StrategyInfo> selectAll();

    /**
     * 根据交易所平台查询可用的策略列表
     * @param footplate 交易所平台 (null=全部, 0=币安, 1=gate)
     * @return 可用策略列表
     */
    List<StrategyInfo> selectByFootplate(@Param("footplate") Integer footplate);

/**
 * 根据主键选择性更新策略信息
 * 该方法会根据传入的StrategyInfo对象中的非空字段来更新数据库中对应的记录
 *
 * @param record 包含要更新的策略信息的对象，只有非空字段会被用于更新操作
 * @return 返回更新的行数，通常为1表示更新成功，0表示没有记录被更新
 */
    int updateByPrimaryKeySelective(StrategyInfo record);

/**
 * 根据主键更新策略信息，包括BLOB字段（大文本字段）
 *
 * @param record 包含更新策略信息的StrategyInfo对象，其中应包含主键和所有需要更新的字段
 * @return 受影响的行数，通常为1表示更新成功，0表示没有记录被更新
 */
    int updateByPrimaryKeyWithBLOBs(StrategyInfo record);

/**
 * 根据主键更新策略信息
 *
 * @param record 包含更新策略信息的对象，其中应包含要更新的策略ID和其他需要更新的字段
 * @return 更新影响的行数，通常返回1表示更新成功，0表示未找到对应记录
 */
    int updateByPrimaryKey(StrategyInfo record);
}