package com.example.core.common.vo.product;

import lombok.Data;

/**
 * 交易所列表 VO
 */
@Data
public class ExchangeListVO {

    /** API Key ID */
    private Integer id;

    /** 交易所平台(0:币安,1:gate) */
    private Integer footplate;

    /** 平台名称 */
    private String footplateName;

    /** 类型(0:现货,1:期货) */
    private Integer type;

    /** API Key名称 */
    private String name;

    /** 状态(0:正常,1:禁用) */
    private Integer status;

    /** 状态名称 */
    private String statusName;
}
