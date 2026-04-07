package com.example.core.mainbody.service;

import com.example.core.common.entity.AdminInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.user.*;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface UserService {

    /**
     * 用户注册
     * @param registerSO
     * @return
     */
    ResultMessage register(RegisterSO registerSO);

    /**
     * 验证手机号是否存在
     * @param phone
     * @return
     */
    ResultMessage verifyPhone(String phone);

    /**
     * 更新密码
     * @param updatePwdSO
     * @return
     */
    ResultMessage updatePwd(UpdatePwdSO updatePwdSO, String openId);

    /**
     * 登录
     * @param loginSO
     * @return
     */
    ResultMessage login(LoginSO loginSO);

    /**
     * 分页查询用户登录日志
     * @param userId
     * @param queryUserLoginListSO
     * @return
     */
    ResultMessage queryUserLoginList(String userId,QueryUserLoginListSO queryUserLoginListSO);

    /**
     * 分页查询用户操作日志
     * @param userId
     * @param queryUserLogListSO
     * @return
     */
    ResultMessage queryUserLogList(String userId, QueryUserLogListSO queryUserLogListSO);



    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    ResultMessage getUserInfo(String userId);

    /**
     * 更新用户信息
     * @param userId
     * @param updateUserInfoSO
     * @return
     */
    ResultMessage updateUserInfo(String userId,UpdateUserInfoSO updateUserInfoSO);



    /**
     * 获取用户折扣
     * @param nodeId
     * @param type
     * @param client
     * @param userId
     * @return
     */
    BigDecimal getDiscount(Integer nodeId, Integer type, String client, String userId);




    /**
     * 更新用户备注信息
     * @param updateUserRemarkSO
     * @return
     */
    ResultMessage updateUserRemark(UpdateUserRemarkSO updateUserRemarkSO);

    /**
     * 更新用户推荐人信息
     * @param updateProSo
     * @return
     */
    ResultMessage updatePro(UpdateProSo updateProSo);

}
