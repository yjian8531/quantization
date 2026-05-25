package com.example.core.common.mapper;

import com.example.core.common.entity.ApikeyInfo;
import com.example.core.common.vo.product.ExchangeListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ApikeyInfoMapper接口
 * 提供了ApiKey信息相关的数据库操作方法
 */
public interface ApikeyInfoMapper {
    /**
     * 根据主键删除APIKey信息
     * @param id APIKey的主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条完整的APIKey记录
     * @param record 包含完整APIKey信息的对象
     * @return 插入的记录数
     */
    int insert(ApikeyInfo record);

    /**
     * 插入一条APIKey记录（只插入非空字段）
     * @param record 包含APIKey信息的对象（只设置需要插入的字段）
     * @return 插入的记录数
     */
    int insertSelective(ApikeyInfo record);

    /**
     * 根据主键查询APIKey信息
     * @param id APIKey的主键ID
     * @return 包含APIKey信息的对象
     */
    ApikeyInfo selectByPrimaryKey(Integer id);

    /**
     * 根据主键更新APIKey信息（只更新非空字段）
     * @param record 包含APIKey信息的对象（只设置需要更新的字段）
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(ApikeyInfo record);

    /**
     * 根据主键更新APIKey信息（更新所有字段）
     * @param record 包含完整APIKey信息的对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(ApikeyInfo record);

    /** 查询用户可用交易所列表 */
    List<ExchangeListVO> selectUserExchangeList(@Param("userId") String userId);

    /** 根据ID和用户ID查询APIKey */
    ApikeyInfo selectByIdAndUserId(@Param("id") Integer id, @Param("userId") String userId);

}