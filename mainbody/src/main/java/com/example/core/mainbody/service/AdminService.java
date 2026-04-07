package com.example.core.mainbody.service;

import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.admin.*;


public interface AdminService {

    /**
     * 登录
     * @param loginSO
     * @return
     */
    ResultMessage login(LoginSO loginSO);

    /**
     * 获取系统操作日志列表
     * @param queryAdminLogSO
     * @return
     */
    ResultMessage queryAdminLog(QueryAdminLogSO queryAdminLogSO);





}
