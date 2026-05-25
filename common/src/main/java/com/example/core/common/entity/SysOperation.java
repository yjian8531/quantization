package com.example.core.common.entity;

import java.util.Date;
/**
 * 系统操作表
 * 该类用于存储系统操作相关的数据信息
 */
public class SysOperation {
    // 操作ID，唯一标识
    private Integer id;

    // 操作类型，限制类型(0:无限制,1:两秒内不可重复)
    private Integer type;

    // 操作
    private String tail;

    // 操作名
    private String alias;

    // 操作状态，状态(0:正常,1:失效)
    private Integer status;

    // 操作备注
    private String remark;

    // 创建时间
    private Date createTime;

    /**
     * 获取操作ID
     * @return 返回操作ID值
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置操作ID
     * @param id 要设置的操作ID值
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取操作类型
     * @return 返回操作类型值
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置操作类型
     * @param type 要设置的操作类型值
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取操作尾部信息
     * @return 返回操作尾部信息字符串
     */
    public String getTail() {
        return tail;
    }

    /**
     * 设置操作尾部信息
     * @param tail 要设置的操作尾部信息字符串
     */
    public void setTail(String tail) {
        this.tail = tail == null ? null : tail.trim();
    }

    /**
     * 获取操作别名
     * @return 返回操作别名字符串
     */
    public String getAlias() {
        return alias;
    }

    /**
     * 设置操作别名
     * @param alias 要设置的操作别名字符串
     */
    public void setAlias(String alias) {
        this.alias = alias == null ? null : alias.trim();
    }

    /**
     * 获取操作状态
     * @return 返回操作状态值
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置操作状态
     * @param status 要设置的操作状态值
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取操作备注
     * @return 返回操作备注字符串
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置操作备注
     * @param remark 要设置的操作备注字符串
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取创建时间
     * @return 返回创建时间对象
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param createTime 要设置的创建时间对象
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}