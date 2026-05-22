package com.example.core.mainbody.so.strategy;

/**
 * 查询策略订单请求参数
 */
public class QueryStrategyOrderSO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 状态：0=启动中，1=运行中，2=暂停，3=已结束
     */
    private Integer status;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
