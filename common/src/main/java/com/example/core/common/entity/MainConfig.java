package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 主机配置表
 */
@Data
public class MainConfig {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 主机平台标签
     */
    private String label;

    /**
     * 账号
     */
    private String account;

    /**
     * KEY
     */
    private String keyNo;

    /**
     * 私钥
     */
    private String keySecret;

    /**
     * 平台区域标识
     */
    private String region;

    /**
     * 分区标识
     */
    private String zone;

    /**
     * 镜像快照
     */
    private String snapshot;

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
