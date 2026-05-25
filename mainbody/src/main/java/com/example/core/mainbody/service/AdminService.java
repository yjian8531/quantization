package com.example.core.mainbody.service;

import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.admin.*;


/**
 * 管理员服务接口
 * 定义了管理员相关的业务操作方法
 */
public interface AdminService {

    /**
     * 登录方法
     * @param loginSO 登录信息封装对象，包含用户名、密码等信息
     * @return 返回操作结果，包含登录状态和相关信息
     */
    ResultMessage login(LoginSO loginSO);

    /**
     * 获取系统操作日志列表方法
     * @param queryAdminLogSO 查询条件封装对象，包含时间范围、操作类型等查询条件
     * @return 返回操作结果，包含日志列表和分页信息
     */
    ResultMessage queryAdminLog(QueryAdminLogSO queryAdminLogSO);





}
