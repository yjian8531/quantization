package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.AdminInfo;
import com.example.core.common.utils.RedisUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.AdminService;
import com.example.core.mainbody.so.admin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    /**
     * 登录
     * @param loginSO
     * @return
     */
    @PostMapping(value = "/login",produces = {"application/json"})
    public ResultMessage login(@RequestBody LoginSO loginSO){
        return adminService.login(loginSO);
    }


//    /**
//     * 获取平台汇总数据
//     * @return
//     */
//    @GetMapping(value = "/get/data/collect",produces = {"application/json"})
//    public ResultMessage getDataCollect(){
//        return adminService.getDataCollect();
//    }
//
//
//    /**
//     * 查询国家产品数量
//     * @param queryCountryNumSO
//     * @return
//     */
//    @PostMapping(value = "/query/country/num",produces = {"application/json"})
//    public ResultMessage queryCountryNum(@RequestBody QueryCountryNumSO queryCountryNumSO){
//        return adminService.queryCountryNum(queryCountryNumSO);
//    }

    /**
     * 获取系统操作日志列表
     * @param queryAdminLogSO
     * @return
     */
    @PostMapping(value = "/query/operation/log",produces = {"application/json"})
    public ResultMessage queryAdminLog(@RequestBody QueryAdminLogSO queryAdminLogSO){
        return adminService.queryAdminLog(queryAdminLogSO);
    }

    /**
     * 退出登录
     * @return
     */
    @GetMapping(value = "/exit",produces = {"application/json"})
    public ResultMessage exit(){
        AdminInfo adminInfo = this.getLoginAdmin();
        if(adminInfo != null){
            RedisUtil.del(adminInfo.getAdminId());
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE,ResultMessage.SUCCEED_MSG);
    }


//    /**
//     * 查询充值消费统计集
//     * @param querySummaryListSO
//     * @return
//     */
//    @PostMapping(value = "/query/summary/list",produces = {"application/json"})
//    public ResultMessage querySummaryList(@RequestBody QuerySummaryListSO querySummaryListSO){
//        return adminService.querySummaryList(querySummaryListSO);
//    }

//
//    /**
//     * 获取活跃客户统计集
//     * @param queryTotalUserSO
//     * @return
//     */
//    @PostMapping(value = "/query/total/user",produces = {"application/json"})
//    public ResultMessage queryTotalUser(@RequestBody QueryTotalUserSO queryTotalUserSO){
//        return adminService.queryTotalUser(queryTotalUserSO);
//    }




}
