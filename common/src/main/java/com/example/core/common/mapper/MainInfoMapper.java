package com.example.core.common.mapper;

import com.example.core.common.entity.MainInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MainInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MainInfo record);

    int insertSelective(MainInfo record);

    MainInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MainInfo record);

    int updateByPrimaryKey(MainInfo record);

    /**
     * 根据主机编号查询主机信息
     * @param mainNo 主机编号
     * @return 主机信息
     */
    MainInfo selectByMainNo(@Param("mainNo") String mainNo);

    /**
     * 根据服务编号查询主机信息
     * @param serviceNo 服务编号
     * @return 主机信息
     */
    MainInfo selectByServiceNo(@Param("serviceNo") String serviceNo);

    /**
     * 根据配置ID查询主机信息列表
     * @param configId 配置ID
     * @return 主机信息列表
     */
    List<MainInfo> selectByConfigId(@Param("configId") Integer configId);
}