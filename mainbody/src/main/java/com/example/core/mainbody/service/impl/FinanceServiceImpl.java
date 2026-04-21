package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.CommissionDetail;
import com.example.core.common.entity.FinanceDetail;
import com.example.core.common.entity.UserDiscount;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.mapper.*;
import com.example.core.common.so.finance.QueryBillListAdminSO;
import com.example.core.common.so.finance.QueryCommissionListSO;
import com.example.core.common.utils.DateUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.vo.finance.*;
import com.example.core.mainbody.service.FinanceService;
import com.example.core.mainbody.so.finance.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private FinanceDetailMapper financeDetailMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserProMapper userProMapper;
    @Autowired
    private CommissionDetailMapper commissionDetailMapper;
    @Autowired
    private UserDiscountMapper userDiscountMapper;
    /**
     * 获取账单总览
     * 对应原型图顶部的卡片数据：本周入金、本周出金、本周分润、总计笔数
     */
    @Override
    public ResultMessage getBillOverview(String userId) {
        // 1. 计算时间范围：获取本周的起始时间（周一 00:00:00）到当前时间
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        // 将当前日历时间向前推算至本周一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // 清除时分秒，确保从周一的 0 点开始计算
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date weekStart = calendar.getTime();

        // 将 Date 对象格式化为数据库可识别的字符串格式
        String startTime = DateUtil.DateToString(weekStart, "yyyy-MM-dd HH:mm:ss");
        String endTime = DateUtil.DateToString(now, "yyyy-MM-dd HH:mm:ss");

        // 2. 执行聚合查询：统计用户在指定时间范围内的各项财务数据
        // 查询本周总收入（所有 direction=0 的记录）
        BigDecimal weekIncome = financeDetailMapper.selectWeekIncome(userId, startTime, endTime);
        // 查询本周总支出（所有 direction=1 的记录）
        BigDecimal weekExpense = financeDetailMapper.selectWeekExpense(userId, startTime, endTime);
        // 查询本周分润（仅统计 tag 为 commission 和 renew 的收入）
        BigDecimal weekCommission = financeDetailMapper.selectWeekCommission(userId, startTime, endTime);

        // 3. 获取总账单笔数：使用 COUNT 统计而不是查询全表，提升性能
        Integer totalCount = financeDetailMapper.selectBillCountByUserId(userId);

        // 4. 封装 VO 对象：将查询结果组合成前端需要的格式
        BillOverviewVO vo = new BillOverviewVO();
        vo.setWeekIncome(weekIncome);
        vo.setWeekExpense(weekExpense);
        vo.setWeekCommission(weekCommission);
        vo.setTotalCount(totalCount);

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, vo);
    }

    /**
     * 查询账单列表
     * 对应原型图下方的流水列表，支持按标签（入金/出金等）和时间筛选
     */
    @Override
    public ResultMessage queryBillList(String userId, QueryDetailListSO queryDetailListSO) {
        // 1. 开启分页拦截
        PageHelper.startPage(queryDetailListSO.getPageNum(), queryDetailListSO.getPageSize());

        // 2. 执行分页查询，SQL层直接完成tag到中文标题/标签的转换
        Page<BillListVO> page = (Page<BillListVO>) financeDetailMapper.selectBillListByUserIdWithConvert(
                userId,
                queryDetailListSO.getDirection(),
                queryDetailListSO.getTags(),
                queryDetailListSO.getStartTime(),
                queryDetailListSO.getEndTime()
        );

        // 3. 组装返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("list", page.getResult());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 获取充值总量概览
     * 对应原型图顶部卡片：累计充值金额、累计充值次数
     */
    @Override
    public ResultMessage getRechargeOverview(String userId) {
        // 1. 查询累计充值金额（仅统计 tag='topup' 且 status=1 的记录）
        BigDecimal totalAmount = financeDetailMapper.selectTotalRecharge(userId);
        // 2. 查询累计充值次数（仅统计 tag='topup' 且 status=1 的记录数量）
        Integer totalCount = financeDetailMapper.selectRechargeCount(userId);

        // 3. 封装概览VO对象
        RechargeOverviewVO vo = new RechargeOverviewVO();
        vo.setTotalAmount(totalAmount);
        vo.setTotalCount(totalCount);

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, vo);
    }

    /**
     * 查询充值记录列表
     * 对应原型图下方的充值流水列表，展示每条充值的金额、交易类型、链类型、时间和哈希
     */
    @Override
    public ResultMessage queryRechargeList(String userId, QueryRechargeListSO queryRechargeListSO) {
        // 1. 开启分页
        PageHelper.startPage(queryRechargeListSO.getPageNum(), queryRechargeListSO.getPageSize());
        // 2. 执行查询，SQL层直接从 coin_type 和 chain_type 字段读取数据
        Page<RechargeRecordVO> page = (Page<RechargeRecordVO>) financeDetailMapper.selectRechargeListWithConvert(userId);
        // 3. 组装返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("list", page.getResult());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    //下面为推广模块

//    /**
//     * 查询推广信息统计
//     * 对应原型图顶部区域：推广链接 + 6 个指标统计
//     */
//    @Override
//    public ResultMessage queryPromotionStats(String userId) {
//        // 1. 查询用户信息，获取推广码
//        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
//        if (userInfo == null) {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "用户不存在");
//        }
//
//        PromotionStatsVO stats = new PromotionStatsVO();
//        String inviteCode = userInfo.getMarket() != null ? userInfo.getMarket() : "CF1DCE";
//        stats.setInviteCode(inviteCode);
//        stats.setInviteLink("https://www.xxxx.com/register/mark?code=" + inviteCode);
//
//        // 2. 统计总团队人数
//        stats.setTotalTeamCount(userProMapper.countTotalSubUsers(userId));
//
//        // 3. 统计当月数据（内联计算当月第一天）
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        String monthStart = DateUtil.DateToString(calendar.getTime(), "yyyy-MM-dd 00:00:00");
//
//        stats.setMonthlyNewUsers(userProMapper.countMonthlyNewUsers(userId, monthStart));
//        stats.setTeamPerformance(commissionDetailMapper.sumTeamConsumption(userId));
//        stats.setTeamProfit(commissionDetailMapper.sumTeamCommission(userId));
//        stats.setMonthlyPerformance(commissionDetailMapper.sumMonthlyConsumption(userId, monthStart));
//        stats.setMonthlyProfit(commissionDetailMapper.sumMonthlyCommission(userId, monthStart));
//
//        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, stats);
//    }
    //下面为推广模块

    /**
     * 查询推广信息统计
     * 返回用户推广码，推广链接由前端拼接
     */
    @Override
    public ResultMessage queryPromotionStats(String userId) {
        // 1. 查询用户信息，获取推广码
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "用户不存在");
        }
        // 2. 返回推广码（前端根据推广码自行拼接链接）
        PromotionStatsVO stats = new PromotionStatsVO();
        stats.setInviteCode(userInfo.getMarket());

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, stats);
    }



    @Override
    public ResultMessage queryRewardList(String userId, PromotionRewardSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<CommissionDetail> page = (Page<CommissionDetail>) commissionDetailMapper.selectRewardList(userId, so.getType());

        // 使用 Map 替代 switch，结构更清晰
        Map<Integer, String[]> typeMap = new HashMap<>();
        typeMap.put(2, new String[]{"邀请激活", "注册", "首开认购金额 $"});
        typeMap.put(3, new String[]{"托管达标奖励", "托管", "统计口径内托管资产 $"});
        typeMap.put(4, new String[]{"量化运行分润", "分润", "本季度可分润基数 $"});

        List<PromotionRewardVO> voList = new ArrayList<>();
        for (CommissionDetail detail : page.getResult()) {
            PromotionRewardVO vo = new PromotionRewardVO();
            vo.setAmount(detail.getCommission());
            vo.setCreateTime(detail.getCreateTime());

            String[] config = typeMap.get(detail.getType());
            if (config != null) {
                vo.setTitle(config[0]);
                vo.setTag(config[1]);
                vo.setDesc(config[2] + (detail.getConsumption() != null ? detail.getConsumption() : "0.00"));
            } else {
                vo.setTitle("其他奖励");
                vo.setTag("");
                vo.setDesc("");
            }
            voList.add(vo);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", voList);
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 查询推广用户列表
     * 对应原型图第二个 Tab：推广用户
     */
    @Override
    public ResultMessage queryPromotionUsers(String userId, PromotionRewardSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<Map<String, Object>> page = (Page<Map<String, Object>>) userProMapper.selectPromotionUsers(userId);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 查询财务明细列表（管理端）
     */
    @Override
    public ResultMessage queryBillListForAdmin(QueryBillListAdminSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        List<FinanceDetail> list = financeDetailMapper.selectAdminBillList(so);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        resultMap.put("total", ((Page<?>) list).getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 查询财务明细详情（管理端）
     */
    @Override
    public ResultMessage getBillDetail(Integer id) {
        FinanceDetail detail = financeDetailMapper.selectByPrimaryKey(id);
        if (detail == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "记录不存在");
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detail);
    }

    /**
     * 查询返佣记录列表（管理端）
     */
    @Override
    public ResultMessage queryCommissionList(QueryCommissionListSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        List<CommissionDetail> list = commissionDetailMapper.selectAdminCommissionList(so);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        resultMap.put("total", ((Page<?>) list).getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 查询折扣配置列表（管理端）
     */
    @Override
    public ResultMessage queryDiscountList(QueryDiscountListSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", so.getUserId());
        paramMap.put("email", so.getEmail());
        Page<UserDiscount> page = (Page<UserDiscount>) userDiscountMapper.selectList(paramMap);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 新增/修改折扣配置（管理端）
     */
    @Override
    @Transactional
    public ResultMessage saveDiscount(UserDiscount discount) {
        discount.setUpdateTime(new Date());
        if (discount.getId() != null) {
            userDiscountMapper.updateByPrimaryKeySelective(discount);
        } else {
            discount.setCreateTime(new Date());
            userDiscountMapper.insertSelective(discount);
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "保存成功");
    }

    /**
     * 删除折扣配置（管理端）
     */
    @Override
    @Transactional
    public ResultMessage deleteDiscount(Integer id) {
        userDiscountMapper.deleteByPrimaryKey(id);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
    }
}
