package com.example.core.common.entity;

import lombok.Data;

import java.util.Date;
@Data
public class UserPro {
    private Integer id;

    private String userId;

    private String proUserId;

    private Integer status;

    private String remark;

    private Date createTime;

}