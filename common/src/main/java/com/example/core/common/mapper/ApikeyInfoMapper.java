package com.example.core.common.mapper;

import com.example.core.common.entity.ApikeyInfo;
import com.example.core.common.vo.product.ExchangeListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApikeyInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApikeyInfo record);

    int insertSelective(ApikeyInfo record);

    ApikeyInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ApikeyInfo record);

    int updateByPrimaryKey(ApikeyInfo record);

    /** 查询用户可用交易所列表 */
    List<ExchangeListVO> selectUserExchangeList(@Param("userId") String userId);

    /** 根据ID和用户ID查询APIKey */
    ApikeyInfo selectByIdAndUserId(@Param("id") Integer id, @Param("userId") String userId);

}