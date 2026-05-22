package com.example.core.common.mapper;

import com.example.core.common.entity.MainConfig;

public interface MainConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MainConfig record);

    int insertSelective(MainConfig record);

    MainConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MainConfig record);

    int updateByPrimaryKey(MainConfig record);

    /**
     * 查询状态正常的主机配置（取第一条）
     * @return 主机配置
     */
    MainConfig selectNormalConfig();
}