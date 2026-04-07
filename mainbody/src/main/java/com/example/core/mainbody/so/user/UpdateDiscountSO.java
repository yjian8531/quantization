package com.example.core.mainbody.so.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新用户折扣
 */
@Data
public class UpdateDiscountSO {

    /** 折扣ID **/
    private Integer id;
    /** 折扣 **/
    private BigDecimal discount;

    /** 节点ID **/
    private Integer nodeId;

    /** 类型(0:上传视频,1:直播专线) **/
    private Integer type;

    /** 客户端(shadowroket,v2ray) **/
    private String client;

    /** 用户ID **/
    private String userId;

    /** 状态(0:正常,1:禁用) **/
    private Integer status;

    /** 备注 **/
    private String remark;

}
