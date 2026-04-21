package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 查询提现列表SO(管理员)
 */
@Data
public class QueryWithdrawalListAdminSO {

    /** 用户账号 **/
    private String account;

    /** 状态(0:申请中,1:审核失败,2:提现成功) **/
    private Integer status;

    /** 提现编号 **/
    private String withdrawalNo;

    /** 开始时间 **/
    private String startTime;

    /** 结束时间 **/
    private String endTime;

    private Integer pageNum;

    private Integer pageSize;
}
