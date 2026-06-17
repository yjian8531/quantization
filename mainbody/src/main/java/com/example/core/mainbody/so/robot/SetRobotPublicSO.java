package com.example.core.mainbody.so.robot;

import lombok.Data;

/**
 * 设置机器人公开状态请求参数
 */
@Data
public class SetRobotPublicSO {
    /** 订单编号 */
    private String orderNo;
    /** 是否公开(0:不公开,1:公开) */
    private Integer pub;
}
