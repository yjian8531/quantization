package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;
import lombok.Data;
import java.util.Date;
/**
 * 用户产品关联实体类
 * 用于存储用户与产品之间的关联信息
 */
@Data
public class UserPro {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 产品用户ID
     */
    private String proUserId;

    /**
     * 状态标识
     */
    private Integer status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

}