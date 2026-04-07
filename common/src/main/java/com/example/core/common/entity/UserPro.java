package com.example.core.common.entity;

import java.util.Date;

public class UserPro {
    private Integer id;

    private String userId;

    private String proUserId;

    private String proUserAccount;

    private String userAccount;

    private Integer status;

    private String remark;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getProUserId() {
        return proUserId;
    }

    public void setProUserId(String proUserId) {
        this.proUserId = proUserId == null ? null : proUserId.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getProUserAccount() {
        return proUserAccount;
    }

    public void setProUserAccount(String proUserAccount) {
        this.proUserAccount = proUserAccount;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }
}