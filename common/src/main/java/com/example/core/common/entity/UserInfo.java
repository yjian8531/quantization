package com.example.core.common.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class UserInfo {
    /**id*/
    private Integer id;

    /** 用户id**/
    private String userId;

    /** 账号**/
    private String account;

    /** 登录密码**/
    private String loginPwd;

    /** 昵称**/
    private String nickName;

    /** 手机号**/
    private String phone;

    /** 邮箱**/
    private String email;

    /** 推广码**/
    private String market;

    /** 用户类型**/
    private Integer type;

    /** 折扣**/
    private BigDecimal discount;

    /** 状态**/
    private Integer status;

    /** 登录限制**/
    private Integer loginLimit;

    /** 登录次数**/
    private Date loginTime;

    /** 备注**/
    private String remark;

    /** 创建时间**/
    private Date createTime;

    /** 更新时间**/
    private Date updateTime;

    /** 登录ip**/
    private String loginIp;

    /** 登录token**/
    private String token;

    /** 微信**/
    private Integer wx;

    /** 头像**/
    private String avatar;

}