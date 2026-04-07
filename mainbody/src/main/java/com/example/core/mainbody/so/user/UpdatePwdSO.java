package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 修改密码SO
 */
@Data
public class UpdatePwdSO {

    /** 短信验证码 **/
    private String code;
    /** 邮箱 **/
    private String email;
    /** 新密码 **/
    private String newPwd;

}
