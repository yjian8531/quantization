package com.example.core.common.so.finance;

import lombok.Data;

/**
 * 返佣记录查询参数（管理端）
 */
@Data
public class QueryCommissionListSO {
    /**
     * 当前页码
     */
    private Integer pageNum;
    /**
     * 每页数量
     */
    private Integer pageSize;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 类型(0:购买返佣,1:续费返佣,2邀请激活,3托管达标,4量化分润)
     */
    private Integer type;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
}
