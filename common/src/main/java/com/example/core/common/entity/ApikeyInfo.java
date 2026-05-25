package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 交易所APIKey记录表
 */
@Data
public class ApikeyInfo {
    /**
     * id
     */
    private Integer id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 交易所平台(0:币安,1:gate)
     */
    private Integer footplate;

    /**
     * 类型(0:现货,1:期货)
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * APP_key
     */
    private String apikey;

    /**
     * APP_secret
     */
    private String secret;

    /**
     * 状态(0:正常,1:禁用)
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新时间
     */
    private Date updateTime;

}
