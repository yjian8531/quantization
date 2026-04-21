package com.example.core.mainbody.service;

import com.example.core.common.entity.UserDiscount;
import com.example.core.common.so.finance.QueryBillListAdminSO;
import com.example.core.common.so.finance.QueryCommissionListSO;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.finance.*;

public interface FinanceService {

    /**
     * 查询用户财务明细列表  财务账单记录模块
     */
    ResultMessage queryBillList(String userId, QueryDetailListSO queryDetailListSO);

    /**
     * 获取用户资产概览    财务账单记录模块
     */
    ResultMessage getBillOverview(String userId);

    /**
     * 获取充值总量概览  财务充值记录模块
     */
    ResultMessage getRechargeOverview(String userId);

    /**
     * 查询充值记录列表  财务充值记录模块
     */
    ResultMessage queryRechargeList(String userId, QueryRechargeListSO queryRechargeListSO);


    //下面为推广模块

    /**
     * 查询推广信息统计
     * 对应原型图顶部区域：推广链接 + 6 个指标统计
     */
    ResultMessage queryPromotionStats(String userId);

    /**
     * 查询推广奖励列表（分页）
     * 对应原型图底部列表：邀请激活/托管达标/量化分润
     */
    ResultMessage queryRewardList(String userId, PromotionRewardSO so);


    /** 查询推广用户列表 用户点击推广消息后 点击推广用户 */
    ResultMessage queryPromotionUsers(String userId, PromotionRewardSO so);

    /** 查询财务明细列表（管理端） */
    ResultMessage queryBillListForAdmin(QueryBillListAdminSO so);

    /** 查询财务明细详情（管理端） */
    ResultMessage getBillDetail(Integer id);

    /** 查询返佣记录列表（管理端） */
    ResultMessage queryCommissionList(QueryCommissionListSO so);

    /** 查询折扣配置列表（管理端） */
    ResultMessage queryDiscountList(QueryDiscountListSO so);

    /** 新增/修改折扣配置（管理端） */
    ResultMessage saveDiscount(UserDiscount discount);

    /** 删除折扣配置（管理端） */
    ResultMessage deleteDiscount(Integer id);

}
