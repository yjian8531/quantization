package com.example.core.mainbody.controller;
import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.UserDiscount;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.so.finance.QueryBillListAdminSO;
import com.example.core.common.so.finance.QueryCommissionListSO;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.FinanceService;
import com.example.core.mainbody.so.finance.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 财务模块Controller
 */
@Slf4j
@RestController
@RequestMapping("/finance")
public class FinanceController extends BaseController {

    @Autowired
    private FinanceService financeService;

    /**
     * 查询用户财务明细列表  账单模块
     */
    @PostMapping(value = "/bill/list", produces = {"application/json"})
    public ResultMessage queryBillList(@RequestBody QueryDetailListSO queryDetailListSO) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryBillList(userInfo.getUserId(), queryDetailListSO);
    }

    /**
     * 获取用户财务总览  账单模块
     */
    @GetMapping(value = "/bill/overview", produces = {"application/json"})
    public ResultMessage getBillOverview() {
        UserInfo userInfo = this.getLoginUser();
        return financeService.getBillOverview(userInfo.getUserId());
    }

    /**
     * 获取充值总量概览
     */
    @PostMapping(value = "/recharge/overview", produces = {"application/json"})
    public ResultMessage getRechargeOverview() {
        UserInfo userInfo = this.getLoginUser();
        return financeService.getRechargeOverview(userInfo.getUserId());
    }

    /**
     * 查询充值记录列表
     */
    @PostMapping(value = "/recharge/list", produces = {"application/json"})
    public ResultMessage queryRechargeList(@RequestBody QueryRechargeListSO queryRechargeListSO) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryRechargeList(userInfo.getUserId(), queryRechargeListSO);
    }

    //下面为充值模块


    /**
     * 查询推广信息统计
     * 对应原型图顶部：推广链接 + 6 个指标（总团队人数、团队业绩、当月推荐等）
     */
    @PostMapping(value = "/stats", produces = {"application/json"})
    public ResultMessage queryPromotionStats() {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryPromotionStats(userInfo.getUserId());
    }

    /**
     * 查询推广奖励列表
     * 对应原型图底部：邀请激活/托管达标/量化分润明细列表
     */
    @PostMapping(value = "/reward/list", produces = {"application/json"})
    public ResultMessage queryRewardList(@RequestBody PromotionRewardSO so) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryRewardList(userInfo.getUserId(), so);
    }

    /**
     * 查询推广用户列表
     * 对应原型图"推广用户"Tab
     */
    @PostMapping(value = "/prouser/list", produces = {"application/json"})
    public ResultMessage queryPromotionUsers(@RequestBody PromotionRewardSO so) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryPromotionUsers(userInfo.getUserId(), so);
    }


    /** 查询财务明细列表（管理端） */
    @PostMapping(value = "/admin/bill/list", produces = {"application/json"})
    public ResultMessage queryBillListForAdmin(@RequestBody QueryBillListAdminSO so) {
        return financeService.queryBillListForAdmin(so);
    }

    /** 查询财务明细详情（管理端） */
    @PostMapping(value = "/admin/bill/detail", produces = {"application/json"})
    public ResultMessage getBillDetail(@RequestParam Integer id) {
        return financeService.getBillDetail(id);
    }

    /** 查询返佣记录列表（管理端） */
    @PostMapping(value = "/admin/commission/list", produces = {"application/json"})
    public ResultMessage queryCommissionList(@RequestBody QueryCommissionListSO so) {
        return financeService.queryCommissionList(so);
    }

    /** 查询折扣配置列表（管理端） */
    @PostMapping(value = "/admin/discount/list", produces = {"application/json"})
    public ResultMessage queryDiscountList(@RequestBody QueryDiscountListSO so) {
        return financeService.queryDiscountList(so);
    }

    /** 新增/修改折扣配置（管理端） */
    @PostMapping(value = "/admin/discount/save", produces = {"application/json"})
    public ResultMessage saveDiscount(@RequestBody UserDiscount discount) {
        return financeService.saveDiscount(discount);
    }

    /** 删除折扣配置（管理端） */
    @PostMapping(value = "/admin/discount/delete", produces = {"application/json"})
    public ResultMessage deleteDiscount(@RequestParam Integer id) {
        return financeService.deleteDiscount(id);
    }
}
