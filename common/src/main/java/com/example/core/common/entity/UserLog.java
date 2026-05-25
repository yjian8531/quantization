package com.example.core.common.entity;

import java.util.Date;

/**
 * 用户日志实体类
 * 用于存储用户操作日志的相关信息
 */
public class UserLog {
    // 日志ID，唯一标识一条日志记录
    private Integer id;

    // 尾部信息，可能用于存储额外的日志详情
    private String tail;

    // 日志别名，用于标识或分类日志
    private String alias;

    // 用户ID，关联到具体的用户
    private String userId;

    // 备注，用于记录额外的说明信息
    private String remark;

    // 创建时间，记录日志生成的时间点
    private Date createTime;

    /**
     * 获取日志ID
     * @return 日志ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置日志ID
     * @param id 日志ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取尾部信息
     * @return 尾部信息字符串
     */
    public String getTail() {
        return tail;
    }

    /**
     * 设置尾部信息，并进行空值处理和前后空格修剪
     * @param tail 尾部信息字符串
     */
    public void setTail(String tail) {
        this.tail = tail == null ? null : tail.trim();
    }

    /**
     * 获取日志别名
     * @return 日志别名字符串
     */
    public String getAlias() {
        return alias;
    }

    /**
     * 设置日志别名，并进行空值处理和前后空格修剪
     * @param alias 日志别名字符串
     */
    public void setAlias(String alias) {
        this.alias = alias == null ? null : alias.trim();
    }

    /**
     * 获取用户ID
     * @return 用户ID字符串
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户ID，并进行空值处理和前后空格修剪
     * @param userId 用户ID字符串
     */
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    /**
     * 获取备注信息
     * @return 备注信息字符串
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注信息，并进行空值处理和前后空格修剪
     * @param remark 备注信息字符串
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取创建时间
     * @return 创建时间Date对象
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param createTime 创建时间Date对象
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}