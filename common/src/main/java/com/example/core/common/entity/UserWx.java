package com.example.core.common.entity;

import java.util.Date;

/**
 * UserWx类，用于存储微信用户相关信息
 * 该类包含微信用户的ID、用户ID、OpenID、状态、备注和创建时间等属性
 */
public class UserWx {
    // 用户ID，主键
    private Integer id;

    // 用户系统ID
    private String userId;

    // 微信开放平台唯一标识
    private String openId;

    // 用户状态，例如：0-禁用，1-正常
    private Integer status;

    // 备注信息
    private String remark;

    // 创建时间
    private Date createTime;

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置用户ID
     * @param id 用户ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户系统ID
     * @return 用户系统ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户系统ID
     * @param userId 用户系统ID，如果为null则设置为null，否则去除前后空格
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取微信开放平台唯一标识
     * @return 微信开放平台唯一标识
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 设置微信开放平台唯一标识
     * @param openId 微信开放平台唯一标识，如果为null则设置为null，否则去除前后空格
     */
    public void setOpenId(String openId) {
        this.openId = openId == null ? null : openId.trim();
    }

    /**
     * 获取用户状态
     * @return 用户状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置用户状态
     * @param status 用户状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取备注信息
     * @return 备注信息
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注信息
     * @param remark 备注信息，如果为null则设置为null，否则去除前后空格
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取创建时间
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}