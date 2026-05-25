package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 主机信息表
 */
@Data
public class MainInfo {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 主机编号
     */
    private String mainNo;

    /**
     * 主机ID
     */
    private Integer configId;

    /**
     * 服务编号
     */
    private String serviceNo;

    /**
     * 连接主机IP
     */
    private String connectIp;

    /**
     * 连接端口
     */
    private Integer connectPort;

    /**
     * 连接账号
     */
    private String connectAccount;

    /**
     * 连接密码
     */
    private String connectPwd;

    /**
     * 备注
     */
    private String remark;

    /**
     * 到期时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 最后更新时间
     */
    private Date updateTime;


}
