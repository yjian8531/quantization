package com.example.core.common.entity;

import java.util.Date;

public class MainInfo {
    private Integer id;

    private String mainNo;

    private Integer configId;

    private String serviceNo;

    private String connectIp;

    private Integer connectPort;

    private String connectAccount;

    private String connectPwd;

    private String remark;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMainNo() {
        return mainNo;
    }

    public void setMainNo(String mainNo) {
        this.mainNo = mainNo == null ? null : mainNo.trim();
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo == null ? null : serviceNo.trim();
    }

    public String getConnectIp() {
        return connectIp;
    }

    public void setConnectIp(String connectIp) {
        this.connectIp = connectIp == null ? null : connectIp.trim();
    }

    public Integer getConnectPort() {
        return connectPort;
    }

    public void setConnectPort(Integer connectPort) {
        this.connectPort = connectPort;
    }

    public String getConnectAccount() {
        return connectAccount;
    }

    public void setConnectAccount(String connectAccount) {
        this.connectAccount = connectAccount == null ? null : connectAccount.trim();
    }

    public String getConnectPwd() {
        return connectPwd;
    }

    public void setConnectPwd(String connectPwd) {
        this.connectPwd = connectPwd == null ? null : connectPwd.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}