package com.example.core.common.entity;

import java.util.Date;

/**
 * 用户登录实体类
 * 用于存储用户登录的相关信息
 */
public class UserLogin {
    // 用户ID
    private Integer id;

    // 用户登录ID
    private String userId;

    // 用户登录IP地址
    private String loginIp;

    // 登录状态
    private Integer status;

    // 备注信息
    private String remark;

    // 创建时间
    private Date createTime;

    /**
     * 获取用户ID
     * @return 返回用户ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 要设置的用户ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户登录ID
     * @return 返回用户登录ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户登录ID
     * @param userId 要设置的用户登录ID，如果为null则设置为null，否则去除前后空格
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取用户登录IP地址
     * @return 返回用户登录IP地址
     */
    public String getLoginIp() {
        return loginIp;
    }

    /**
     * 设置用户登录IP地址
     * @param loginIp 要设置的用户登录IP地址，如果为null则设置为null，否则去除前后空格
     */
    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp == null ? null : loginIp.trim();
    }

    /**
     * 获取登录状态
     * @return 返回登录状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置登录状态
     * @param status 要设置的登录状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取备注信息
     * @return 返回备注信息
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注信息
     * @param remark 要设置的备注信息，如果为null则设置为null，否则去除前后空格
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取创建时间
     * @return 返回创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param createTime 要设置的创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}