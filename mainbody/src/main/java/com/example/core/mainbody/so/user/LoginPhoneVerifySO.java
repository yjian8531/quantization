package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 获取注册手机验证码
 */
@Data
public class LoginPhoneVerifySO {

    /** 图形验证码 **/
    private String code;
    /** 邮箱 **/
    private String email;

}
