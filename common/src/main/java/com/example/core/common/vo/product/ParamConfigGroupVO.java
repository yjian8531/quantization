package com.example.core.common.vo.product;

import lombok.Data;

import java.util.List;

/**
 * 参数配置分组 VO
 */
@Data
public class ParamConfigGroupVO {

    /** 分组名称（如：资金配置、仓位配置） */
    private String name;

    /** 配置项列表 */
    private List<ParamConfigItemVO> config;
}