package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 *  登录SO
 */
@Data
public class LoginSO {
    /** 邮箱 **/
    private String email;
    /** 密码 **/
    private String pwd;
    /** ip **/
    private String ip;
}
