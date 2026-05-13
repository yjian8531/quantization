package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.vo.product.ExchangeListVO;
import com.example.core.common.vo.product.SymbolListVO;
import com.example.core.common.vo.robot.HistoryPositionVO;
import com.example.core.common.vo.robot.RobotDetailVO;
import com.example.core.common.vo.robot.RobotListVO;
import com.example.core.common.vo.robot.TradeRecordVO;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.so.product.ConfigRobotSO;
import com.example.core.mainbody.so.robot.QueryHistoryPositionSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
import com.example.core.mainbody.so.robot.QueryTradeRecordSO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private ApikeyInfoMapper apikeyInfoMapper;

    @Autowired
    private SymbolInfoMapper symbolInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private UserFinanceMapper userFinanceMapper;

    @Autowired
    private FinanceDetailMapper financeDetailMapper;

    @Autowired
    private OrderPositionMapper orderPositionMapper;


    @Autowired
    private OrderTradeMapper orderTradeMapper;


    /**
     * 配置机器人（创建策略订单）
     * 采用标准扣费流程：1.冻结余额 -> 2.创建记录 -> 3.实际扣款
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage configRobot(String userId, ConfigRobotSO so) {
        OrderProduct product = orderProductMapper.selectByPrimaryKey(so.getProductId());
        if (product == null || product.getStatus() != 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "产品不存在或已下架");
        }

        UserFinance finance = userFinanceMapper.selectByUserId(userId);
        BigDecimal fee = product.getMonthlyFee();

        if (finance == null || finance.getValidNum().compareTo(fee) < 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "可用余额不足，请先充值");
        }

        finance.setValidNum(finance.getValidNum().subtract(fee));
        finance.setFrozenNum(finance.getFrozenNum().add(fee));
        userFinanceMapper.updateByPrimaryKeySelective(finance);

        FinanceDetail detail = new FinanceDetail();
        detail.setUserId(userId);
        detail.setFinanceNo("FIN" + System.currentTimeMillis());
        detail.setType(1);
        detail.setCoinType("USDT");
        detail.setMoneyNum(fee);
        detail.setTag("buy");
        detail.setDirection(1);
        detail.setStatus(0);
        detail.setRemarks("购买" + product.getProductName() + "服务");
        detail.setCreateTime(new Date());
        financeDetailMapper.insertSelective(detail);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo("ORD" + System.currentTimeMillis() + new Random().nextInt(1000));
        orderInfo.setOrderName(product.getProductName());
        orderInfo.setProductId(product.getId());
        orderInfo.setUserId(userId);
        orderInfo.setApikeyId(so.getApikeyId());
        orderInfo.setSymbol(so.getSymbol());
        orderInfo.setNodeTime(so.getNodeTime());
        orderInfo.setParamStr(so.getParamStr());
        orderInfo.setAnnualizedRate(product.getEstimateRate());
        orderInfo.setIncome(BigDecimal.ZERO);
        orderInfo.setIncomeRate(BigDecimal.ZERO);
        orderInfo.setStatus(0);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfoMapper.insertSelective(orderInfo);

        finance.setFrozenNum(finance.getFrozenNum().subtract(fee));
        finance.setTotalNum(finance.getTotalNum().subtract(fee));
        userFinanceMapper.updateByPrimaryKeySelective(finance);

        detail.setStatus(1);
        financeDetailMapper.updateByPrimaryKeySelective(detail);

        product.setBuyCount(product.getBuyCount() == null ? 1 : product.getBuyCount() + 1);
        if (product.getTotalAmount() == null) {
            product.setTotalAmount(BigDecimal.ZERO);
        }
        product.setTotalAmount(product.getTotalAmount().add(fee));
        orderProductMapper.updateByPrimaryKeySelective(product);

        return new ResultMessage(ResultMessage.SUCCEED_CODE, "配置成功，机器人启动中");
    }

    /**
     * 查询用户机器人列表
     */
    @Override
    public ResultMessage queryRobotList(String userId, QueryRobotSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());

        Page<RobotListVO> page = (Page<RobotListVO>) orderInfoMapper.selectUserRobotList(userId, so.getExchange());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 查询用户机器人详情
     */
    @Override
    public ResultMessage queryRobotDetail(String userId, Integer orderId) {
        RobotDetailVO detail = orderInfoMapper.selectRobotDetail(orderId, userId);
        if (detail == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在或无权限查看");
        }

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detail);
    }

    /**
     * 查询用户历史持仓列表
     */
    @Override
    public ResultMessage queryHistoryPositionList(String userId, QueryHistoryPositionSO so) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(so.getOrderId());
        if (orderInfo == null || !orderInfo.getUserId().equals(userId)) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在或无权限查看");
        }

        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<HistoryPositionVO> page = (Page<HistoryPositionVO>) orderPositionMapper.selectHistoryPositionList(orderInfo.getOrderNo());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }
    /**
     * 查询用户交易记录列表
     */
    @Override
    public ResultMessage queryTradeRecordList(String userId, QueryTradeRecordSO so) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(so.getOrderId());
        if (orderInfo == null || !orderInfo.getUserId().equals(userId)) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在或无权限查看");
        }

        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<TradeRecordVO> page = (Page<TradeRecordVO>) orderTradeMapper.selectTradeRecordList(orderInfo.getOrderNo());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 查询用户交易所列表
     */
    @Override
    public ResultMessage queryExchangeList(String userId) {
        List<ExchangeListVO> list = apikeyInfoMapper.selectUserExchangeList(userId);
        if (list == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "用户未绑定交易所");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        resultMap.put("total", list.size());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 查询交易币对列表
     */
    @Override
    public ResultMessage querySymbolList() {
        List<SymbolListVO> list = symbolInfoMapper.selectSymbolList();
        if (list == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "查询失败");
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, list);
    }
}
