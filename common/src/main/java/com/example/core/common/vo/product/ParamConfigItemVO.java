package com.example.core.common.vo.product;

import lombok.Data;

/**
 * 参数配置项 VO
 */
@Data
public class ParamConfigItemVO {

    /** 显示名称（如：本金、杠杆倍数） */
    private String nike;

    /** 字段标识（如：AccountAmount、Lever） */
    private String field;

    /** 单位（如：USDT、倍、%） */
    private String unit;

    /** 实际值（从产品表的 param_config 里取，或前端提交时传入） */
    private Object value;
}