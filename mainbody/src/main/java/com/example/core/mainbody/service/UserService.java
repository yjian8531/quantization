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
     * 验证邮箱是否存在
     * @param email
     * @return
     */
    ResultMessage verifyEmail(String email);

    /**
     * 更新密码
     * @param updatePwdSO
     * @return
     */
    ResultMessage updatePwd(UpdatePwdSO updatePwdSO, String openId);



    /**
     * 修改密码（登录状态下，需要验证旧密码）
     * @param userId 用户ID
     * @param so 修改密码参数
     * @return 操作结果
     */
    ResultMessage updatePassword(String userId, UpdatePasswordSO so);


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


    /**
     * 获取用户资产概览（总资产、累计收益、VIP状态）
     */
    ResultMessage getUserAssetOverview(String userId);
}
