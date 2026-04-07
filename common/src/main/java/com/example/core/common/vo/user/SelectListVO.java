package com.example.core.common.vo.user;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询用户列表VO
 */
@Data
public class SelectListVO {

    /** 记录ID **/
    private String id;
    /** 用户ID **/
    private String userId;
    /** 账号 **/
    private String account;
    /** 昵称 **/
    private String nickName;
    /** 手机 **/
    private String phone;
    /** 邮箱 **/
    private String email;
    /** 推广码 **/
    private String market;
    /** 用户推广类型(0:普通用户推广) **/
    private Integer type;
    /** 状态(0:正常,1:禁用) **/
    private Integer status;
    /** 备注 **/
    private String remark;
    /** 创建时间 **/
    private Date createTime;
    /** 更新时间 **/
    private Date updateTime;
    /** 可用金额 **/
    private BigDecimal validNum;
    /** 折扣设置 **/
    private Integer discountNum;
    /** 产品路线数量 **/
    private Integer productNum;
    /** 推荐人 **/
    private String proUserAccount;
}
