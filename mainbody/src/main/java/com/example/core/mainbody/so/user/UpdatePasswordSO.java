package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 修改密码SO（登录状态下，需要验证旧密码）
 */
@Data
public class UpdatePasswordSO {
    /** 旧密码 **/
    private String oldPwd;
    /** 新密码 **/
    private String newPwd;
}
