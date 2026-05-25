package com.example.core.mainbody.service;

import com.example.core.common.entity.CommissionDetail;
import com.example.core.common.entity.UserDiscount;
import com.example.core.common.entity.UserFinance;
import com.example.core.common.so.finance.QueryBillListAdminSO;
import com.example.core.common.so.finance.QueryCommissionListSO;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.finance.*;

import java.math.BigDecimal;

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



    /**
     * 新增用户财务信息
     * @param userFinance 用户财务信息对象
     * @return 操作结果
     */
    ResultMessage addUserFinance(UserFinance userFinance);

    /**
     * 修改用户财务信息
     * @param userFinance 用户财务信息对象
     * @return 操作结果
     */
    ResultMessage updateUserFinance(UserFinance userFinance);

    /**
     * 删除用户财务信息
     * @param id 主键ID
     * @return 操作结果
     */
    ResultMessage deleteUserFinance(Integer id);

    /**
     * 查询用户财务信息详情
     * @param id 主键ID
     * @return 用户财务信息对象
     */
    ResultMessage getUserFinanceDetail(Integer id);

    /**
     * 根据用户ID查询财务信息
     * @param userId 用户ID
     * @return 用户财务信息对象
     */
    ResultMessage getUserFinanceByUserId(String userId);

    /**
     * 更新用户余额
     * @param userId 用户ID
     * @param tag 操作类型（add：增加,minus：减少,unbind：解冻,seal：冻结）
     * @param amount 金额
     * @return 操作结果
     */
    ResultMessage updateBalance(String userId, String tag, BigDecimal amount);

    /**
     * 分页查询用户财务信息列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param userId 用户ID（可选）
     * @return 用户财务信息列表
     */
    ResultMessage queryUserFinanceList(Integer pageNum, Integer pageSize, String userId);



    /**
     * 新增佣金明细记录
     * @param commissionDetail 佣金明细对象
     * @return 操作结果
     */
    ResultMessage addCommissionDetail(CommissionDetail commissionDetail);

    /**
     * 修改佣金明细记录
     * @param commissionDetail 佣金明细对象
     * @return 操作结果
     */
    ResultMessage updateCommissionDetail(CommissionDetail commissionDetail);

    /**
     * 删除佣金明细记录
     * @param id 主键ID
     * @return 操作结果
     */
    ResultMessage deleteCommissionDetail(Integer id);

    /**
     * 查询佣金明细详情
     * @param id 主键ID
     * @return 佣金明细对象
     */
    ResultMessage getCommissionDetail(Integer id);

    /**
     * 根据用户ID查询佣金明细列表
     * @param userId 用户ID
     * @return 佣金明细列表
     */
    ResultMessage getCommissionDetailsByUserId(String userId);

    /**
     * 分页查询佣金明细列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param userId 用户ID（可选）
     * @param type 类型（可选）
     * @return 佣金明细列表
     */
    ResultMessage queryCommissionDetailList(Integer pageNum, Integer pageSize, String userId, Integer type);
}
