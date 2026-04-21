package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 获取注册邮箱验证码
 */
@Data
public class LoginEmailVerifySO {

    /** 图形验证码 **/
    private String code;
    /** 邮箱 **/
    private String email;
}
