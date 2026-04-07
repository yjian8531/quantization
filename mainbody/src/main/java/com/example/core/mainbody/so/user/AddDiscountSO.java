package com.example.core.mainbody.so.user;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 添加用户折扣SO
 */
@Data
public class AddDiscountSO {

    /** 折扣 **/
    private BigDecimal discount;

    /** 节点ID **/
    private Integer nodeId;

    /** 节点ID List **/
    private List<Integer> nodeIds;

    /** 类型(0:上传视频,1:直播专线) **/
    private Integer type;

    /** 客户端(shadowroket,v2ray) **/
    private String client;

    /** 用户ID **/
    private String userId;

    /** 备注 **/
    private String remark;
}
