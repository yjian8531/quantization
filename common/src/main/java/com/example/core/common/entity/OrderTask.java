package com.example.core.common.entity;

import java.util.Date;

public class OrderTask {
    private Integer id;
    /** 订单编号 **/
    private String orderNo;
    /** 标记(0:机器人服务器创建,1:启动机器人) **/
    private Integer tag;
    /** 备注 **/
    private String remark;
    /** 状态(0:待处理,1:已完成,2:失败) **/
    private Integer status;
    /** 任务创建时间 **/
    private Date createTime;
    /** 任务最后更新时间时间 **/
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Integer getTag() {
        return tag;
    }

    public void setTag(Integer tag) {
        this.tag = tag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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