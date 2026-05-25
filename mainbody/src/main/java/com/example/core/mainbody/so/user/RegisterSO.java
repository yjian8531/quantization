package com.example.core.mainbody.so.user;

import lombok.Data;

/***
 *  用户注册SO
 */
@Data
public class RegisterSO {

    /** 注册验证码 **/
    private String code;
    /** 邮箱 **/
    private String email;
    /** 密码 **/
    private String pwd;
    /** 昵称 **/
    private String name;
    /** 推广码 **/
    private String market;
    /** 类型 0:普通用户,1:公司内部账号 **/
    private Integer type;
    /** 头像 **/
    private String avatar;

}
