package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 查询用户列表SO
 */
@Data
public class QueryListSO {

    /** 账号 **/
    private String account;
    /** 状态(0:正常,1:禁用) **/
    private Integer status;
    /** 备注 **/
    private String remark;
    /** 昵称 **/
    private String nickName;
    /** 创建开始时间 **/
    private String startTime;
    /** 创建结束时间 **/
    private String endTime;

    /**  排序方式
     * 0:时间降序
     * 1:时间升序
     * 2:路线数量降序
     * 3:路线数量升序
     * 4:余额降序
     * 5:余额升序
     **/
    private Integer sort;

    private Integer pageNum;
    private Integer pageSize;


}
