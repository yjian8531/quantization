package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 分页查询用户折扣列表
 */
@Data
public class QueryDiscountListSO {

    /** 节点ID **/
    private Integer nodeId;

    /** 类型(0:上传视频,1:直播专线) **/
    private Integer type;

    /** 客户端(shadowroket,v2ray) **/
    private String client;

    /** 状态(0:正常,1:禁用) **/
    private Integer status;

    private String userId;

    private Integer pageNum;

    private Integer pageSize;

}
