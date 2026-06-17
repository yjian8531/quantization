package com.example.core.mainbody.controller;
import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.CommissionDetail;
import com.example.core.common.entity.UserDiscount;
import com.example.core.common.entity.UserFinance;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.so.finance.QueryBillListAdminSO;
import com.example.core.common.so.finance.QueryCommissionListSO;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.FinanceService;
import com.example.core.mainbody.service.FinanceWalletService;
import com.example.core.mainbody.so.finance.*;
import com.example.core.mainbody.so.financewallet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 财务模块Controller
 */
@Slf4j
@RestController
@RequestMapping("/finance")
public class FinanceController extends BaseController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private FinanceWalletService financeWalletService;

    /**
     * 查询用户余额信息明细列表
     */
    @PostMapping(value = "/bill/list", produces = {"application/json"})
    public ResultMessage queryBillList(@RequestBody QueryDetailListSO queryDetailListSO) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryBillList(userInfo.getUserId(), queryDetailListSO);
    }

    /**
     * 获取用户余额信息总览
     */
    @GetMapping(value = "/bill/overview", produces = {"application/json"})
    public ResultMessage getBillOverview() {
        UserInfo userInfo = this.getLoginUser();
        return financeService.getBillOverview(userInfo.getUserId());
    }

    /**
     * 获取充值总量概览
     */
    @GetMapping(value = "/recharge/overview", produces = {"application/json"})
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
     *
     */
    @GetMapping(value = "/stats", produces = {"application/json"})
    public ResultMessage queryPromotionStats() {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryPromotionStats(userInfo.getUserId());
    }

    /**
     * 查询推广奖励列表
     *
     */
    //对应原型图底部：邀请激活/托管达标/量化分润明细列表
    @PostMapping(value = "/reward/list", produces = {"application/json"})
    public ResultMessage queryRewardList(@RequestBody PromotionRewardSO so) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryRewardList(userInfo.getUserId(), so);
    }

    /**
     * 查询推广用户列表
     */
    // 对应原型图"推广用户"Tab
    @PostMapping(value = "/prouser/list", produces = {"application/json"})
    public ResultMessage queryPromotionUsers(@RequestBody PromotionRewardSO so) {
        UserInfo userInfo = this.getLoginUser();
        return financeService.queryPromotionUsers(userInfo.getUserId(), so);
    }


    /** 查询财务明细列表 */
    @PostMapping(value = "/admin/bill/list", produces = {"application/json"})
    public ResultMessage queryBillListForAdmin(@RequestBody QueryBillListAdminSO so) {
        return financeService.queryBillListForAdmin(so);
    }

    /** 查询财务明细详情 */
    @PostMapping(value = "/admin/bill/detail", produces = {"application/json"})
    public ResultMessage getBillDetail(@RequestParam Integer id) {
        return financeService.getBillDetail(id);
    }

    /** 查询返佣记录列表 */
    @PostMapping(value = "/admin/commission/list", produces = {"application/json"})
    public ResultMessage queryCommissionList(@RequestBody QueryCommissionListSO so) {
        return financeService.queryCommissionList(so);
    }



    /**
     * 删除用户余额信息
     * @param deleteSO 删除请求对象
     * @return 操作结果
     */
    @PostMapping(value = "/userFinance/delete", produces = {"application/json"})
    public ResultMessage deleteUserFinance(@RequestBody UserFinanceDeleteSO deleteSO) {
        log.info("删除用户余额信息请求，ID: {}", deleteSO.getId());
        return financeService.deleteUserFinance(deleteSO.getId());
    }


    /**
     * 查询用户余额信息详情
     * @param detailSO 详情查询请求对象
     * @return 用户余额信息对象
     */
    @PostMapping(value = "/userFinance/detail", produces = {"application/json"})
    public ResultMessage getUserFinanceDetail(@RequestBody IdSO detailSO) {
        log.info("查询用户余额信息详情，ID: {}", detailSO.getId());
        return financeService.getUserFinanceDetail(detailSO.getId());
    }


    /**
     * 根据用户ID查询财务信息
     * @param userIdSO 用户ID查询请求对象
     * @return 用户余额信息对象
     */
    @PostMapping(value = "/userFinance/detail/user", produces = {"application/json"})
    public ResultMessage getUserFinanceByUserId(@RequestBody UserIdSO userIdSO) {
        log.info("根据用户ID查询财务信息，用户ID: {}", userIdSO.getUserId());
        return financeService.getUserFinanceByUserId(userIdSO.getUserId());
    }


    /**
     * 更新用户余额
     * @param balanceSO 余额更新请求对象
     * @return 操作结果
     */
    @PostMapping(value = "/userFinance/update/balance", produces = {"application/json"})
    public ResultMessage updateBalance(@RequestBody UserBalanceUpdateSO balanceSO) {
        log.info("更新用户余额请求，用户ID: {}, 操作类型: {}, 金额: {}",
                balanceSO.getUserId(), balanceSO.getTag(), balanceSO.getAmount());
        return financeService.updateBalance(balanceSO.getUserId(), balanceSO.getTag(), balanceSO.getAmount());
    }


    /**
     * 分页查询用户余额信息列表
     * @param listSO 列表查询请求对象
     * @return 用户余额信息列表
     */
    @PostMapping(value = "/userFinance/list", produces = {"application/json"})
    public ResultMessage queryUserFinanceList(@RequestBody UserFinanceListSO listSO) {
        log.info("查询用户余额信息列表");
        return financeService.queryUserFinanceList(
                listSO.getPageNum() != null ? listSO.getPageNum() : 1,
                listSO.getPageSize() != null ? listSO.getPageSize() : 10,
                listSO.getUserId());
    }


    /**
     * 删除佣金明细记录
     * @param deleteSO 删除请求对象
     * @return 操作结果
     */
    @PostMapping(value = "/commission/delete", produces = {"application/json"})
    public ResultMessage deleteCommissionDetail(@RequestBody CommissionDeleteSO deleteSO) {
        log.info("删除佣金明细记录请求，ID: {}", deleteSO.getId());
        return financeService.deleteCommissionDetail(deleteSO.getId());
    }


    /**
     * 查询佣金明细详情
     * @param detailSO 详情查询请求对象
     * @return 佣金明细对象
     */
    @PostMapping(value = "/commission/detail", produces = {"application/json"})
    public ResultMessage getCommissionDetail(@RequestBody IdSO detailSO) {
        log.info("查询佣金明细详情，ID: {}", detailSO.getId());
        return financeService.getCommissionDetail(detailSO.getId());
    }


    /**
     * 根据用户ID查询佣金明细列表
     * @param userIdSO 用户ID查询请求对象
     * @return 佣金明细列表
     */
    @PostMapping(value = "/commission/list/user", produces = {"application/json"})
    public ResultMessage getCommissionDetailsByUserId(@RequestBody UserIdSO userIdSO) {
        log.info("根据用户ID查询佣金明细列表，用户ID: {}", userIdSO.getUserId());
        return financeService.getCommissionDetailsByUserId(userIdSO.getUserId());
    }


    /**
     * 分页查询佣金明细列表
     * @param listSO 列表查询请求对象
     * @return 佣金明细列表
     */
    @PostMapping(value = "/commission/list", produces = {"application/json"})
    public ResultMessage queryCommissionDetailList(@RequestBody CommissionListSO listSO) {
        log.info("查询佣金明细列表");
        return financeService.queryCommissionDetailList(
                listSO.getPageNum() != null ? listSO.getPageNum() : 1,
                listSO.getPageSize() != null ? listSO.getPageSize() : 10,
                listSO.getUserId(),
                listSO.getType());
    }


// ===================== 管理端接口 =====================

    /**
     * 查询钱包列表（管理端）
     */
    @PostMapping(value = "/admin/wallet/list", produces = {"application/json"})
    public ResultMessage queryFinanceWalletList(@RequestBody QueryFinanceWalletSO so) {
        return financeWalletService.queryFinanceWalletList(so);
    }

    /**
     * 查询钱包详情（管理端）
     */
    @PostMapping(value = "/admin/wallet/detail", produces = {"application/json"})
    public ResultMessage getFinanceWalletDetail(@RequestBody FinanceWalletDetailSO so) {
        return financeWalletService.getFinanceWalletDetail(so);
    }

    /**
     * 新增钱包/导入地址池（管理端）
     */
    @PostMapping(value = "/admin/wallet/add", produces = {"application/json"})
    public ResultMessage addFinanceWallet(@RequestBody AddFinanceWalletSO so) {
        return financeWalletService.addFinanceWallet(so);
    }

    /**
     * 更新钱包状态（管理端）
     */
    @PostMapping(value = "/admin/wallet/update", produces = {"application/json"})
    public ResultMessage updateFinanceWallet(@RequestBody UpdateFinanceWalletSO so) {
        return financeWalletService.updateFinanceWallet(so);
    }

    /**
     * 删除钱包（管理端）
     */
    @PostMapping(value = "/admin/wallet/delete", produces = {"application/json"})
    public ResultMessage deleteFinanceWallet(@RequestBody DeleteFinanceWalletSO so) {
        return financeWalletService.deleteFinanceWallet(so);
    }
}
