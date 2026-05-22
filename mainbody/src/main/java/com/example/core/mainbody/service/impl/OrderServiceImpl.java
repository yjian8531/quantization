package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
<<<<<<< HEAD
import com.example.core.common.utils.*;
import com.example.core.mainbody.service.MainService;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
=======
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
>>>>>>> origin/main
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

<<<<<<< HEAD
@Slf4j
=======
>>>>>>> origin/main
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
<<<<<<< HEAD
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderTradeMapper orderTradeMapper;

    @Autowired
    private OrderPositionMapper orderPositionMapper;

    @Autowired
=======
>>>>>>> origin/main
    private OrderProductMapper orderProductMapper;

    @Autowired
    private ApikeyInfoMapper apikeyInfoMapper;

    @Autowired
<<<<<<< HEAD
    private MainInfoMapper mainInfoMapper;

    @Autowired
    private MainService mainService;

    @Autowired
    private MainConfigMapper mainConfigMapper;


    /**
     * 创建策略订单
     */
    @Transactional
    public ResultMessage createStrategyOrder(CreateStrategyOrderSO so, String userId) {
        try {
            // 1. 校验产品是否存在
            OrderProduct product = orderProductMapper.selectByPrimaryKey(so.getProductId());
            if (product == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "产品不存在");
            }
            if (product.getStatus() != 0) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "产品已下架");
            }

            // 2. 校验APIKey是否存在且属于当前用户
            ApikeyInfo apikeyInfo = apikeyInfoMapper.selectByIdAndUserId(so.getApikeyId(), userId);
            if (apikeyInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "APIKey不存在或不属于当前用户");
            }
            if (apikeyInfo.getStatus() != StrategyConstant.ConfigStatus.NORMAL) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "APIKey已被禁用");
            }

            // 3. 获取可用的主机配置
            MainConfig mainConfig = mainConfigMapper.selectNormalConfig();
            if (mainConfig == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "暂无可用的主机配置");
            }

            // 4. 创建云服务器
            String instanceId = mainService.create(mainConfig);
            if (StringUtils.isEmpty(instanceId)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "创建机器人服务器失败");
            }
            String mainNo = CommonUtil.getRandomStr(12);
            // 3. 创建主机信息记录（初始状态）
            MainInfo mainInfo = new MainInfo();
            mainInfo.setMainNo(mainNo);
            mainInfo.setConfigId(mainConfig.getId());
            mainInfo.setCreateTime(new Date());
            mainInfo.setUpdateTime(new Date());
            mainInfoMapper.insertSelective(mainInfo);

            // 5. 生成订单编号
            String orderNo = CommonUtil.getRandomStr(8);

            // 6. 创建策略订单
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderNo(orderNo);
            orderInfo.setOrderName(product.getProductName());
            orderInfo.setMainNo(mainNo);
            orderInfo.setProductId(so.getProductId());
            orderInfo.setUserId(userId);
            orderInfo.setApikeyId(so.getApikeyId());
            orderInfo.setSymbol(so.getSymbol());
            orderInfo.setNodeTime(so.getNodeTime());
            orderInfo.setParamStr(so.getParamStr());
            orderInfo.setStatus(StrategyConstant.OrderStatus.STARTING);
            orderInfo.setAnnualizedRate(product.getEstimateRate());
            orderInfo.setCreateTime(new Date());
            int i = orderInfoMapper.insertSelective(orderInfo);
            if(i > 0){
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "创建成功，机器人部署中");
            }else{
                return new ResultMessage(ResultMessage.FAILED_CODE, "创建失败");
            }
        } catch (Exception e) {
            log.error("创建策略订单失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "创建失败: " + e.getMessage());
        }
=======
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
>>>>>>> origin/main
    }


    /**
<<<<<<< HEAD
     * 启动策略
     */
    @Transactional
    public ResultMessage startStrategyOrder(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());
        ApikeyInfo apikeyInfo = apikeyInfoMapper.selectByPrimaryKey(orderInfo.getApikeyId());
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }

        try{
            // TODO: 发送恢复指令到云服务器
            String exchange = apikeyInfo.getFootplate() == 0 ? "binance" : "gateio";// binance 或 gateio
            String apiKey = apikeyInfo.getApikey();
            String secret = apikeyInfo.getSecret();

            Map<String, Object> params = (Map<String, Object>)JSONObject.toBean(JSONObject.fromObject(orderInfo.getParamStr()),HashMap.class);

            StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
            String str = strategyUtil.startStrategy(exchange,apiKey,secret,params);
            JSONObject result = JSONObject.fromObject(str);
            if(result.getInt("status") == 0) {
                orderInfo.setStrategyId(result.getJSONObject("data").getString("strategyId"));
                orderInfo.setStatus(StrategyConstant.OrderStatus.RUNNING);
                orderInfo.setUpdateTime(new Date());
                orderInfoMapper.updateByPrimaryKeySelective(orderInfo);

                return new ResultMessage(ResultMessage.SUCCEED_CODE, "成功");

            }else{
                return new ResultMessage(ResultMessage.FAILED_CODE, "启动策略失败",result.getString("msg"));
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResultMessage(ResultMessage.FAILED_CODE, "启动策略异常",e.getMessage());
        }

    }

    /**
     * 停止策略
     * @param orderNo
     * @return
     */
    public ResultMessage stopStrategyOrder(String orderNo){
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());

        StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
        String str = strategyUtil.stopStrategy(orderInfo.getStrategyId());
        JSONObject result = JSONObject.fromObject(str);
        if(result.getInt("status") == 0) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "成功");
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "启动策略失败",result.getString("msg"));
        }
    }


    /**
     * 接收交易日志推送
     */
    @Transactional
    public ResultMessage receiveTradeLog(TradeLogPushSO tradeLog) {
        try {
            // 校验订单是否存在
            OrderInfo orderInfo = orderInfoMapper.selectByStrategyId(tradeLog.getStrategyId());
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
            }

            // 设置创建时间
            if (tradeLog.getCreateTime() == null) {
                tradeLog.setCreateTime(new Date());
            }

            // 保存交易日志
            OrderTrade orderTrade = new OrderTrade();
            orderTrade.setOrderNo(orderInfo.getOrderNo());
            orderTrade.setTradeNo(tradeLog.getTradeNo());
            orderTrade.setTradeBl(tradeLog.getTradeBl());
            orderTrade.setAmount(tradeLog.getAmount());
            orderTrade.setTradeNum(tradeLog.getTradeNum());
            orderTrade.setIncome(tradeLog.getIncome());
            orderTrade.setPrice(tradeLog.getPrice());
            orderTrade.setCreateTime(tradeLog.getCreateTime());
            orderTrade.setUpdateTime(new Date());
            orderTradeMapper.insertSelective(orderTrade);
            if(tradeLog.getTradeNum() != null){
                // 更新订单收益
                updateOrderIncome(orderInfo.getOrderNo());
            }


            return new ResultMessage(ResultMessage.SUCCEED_CODE, "交易日志已接收");

        } catch (Exception e) {
            log.error("接收交易日志失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "接收失败: " + e.getMessage());
        }
    }

    /**
     * 接收仓位信息推送
     */
    @Transactional
    public ResultMessage receivePositionInfo(PositionPushSO position) {
        try {
            // 校验订单是否存在
            OrderInfo orderInfo = orderInfoMapper.selectByStrategyId(position.getStrategyId());
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
            }

            // 设置开始时间
            if (position.getStartTime() == null) {
                position.setStartTime(new Date());
            }

            // 保存仓位信息
            OrderPosition orderPosition = new OrderPosition();
            orderPosition.setOrderNo(orderInfo.getOrderNo());
            orderPosition.setTradeBl(position.getTradeBl());
            orderPosition.setOpenPrice(position.getOpenPrice());
            orderPosition.setClosePrice(position.getClosePrice());
            orderPosition.setTradeNum(position.getTradeNum());
            orderPosition.setIncome(position.getIncome());
            orderPosition.setIncomeRate(position.getIncomeRate());
            orderPosition.setRemark(position.getRemark());
            orderPosition.setStartTime(position.getStartTime());
            orderPosition.setEndTime(position.getEndTime());
            orderPositionMapper.insertSelective(orderPosition);

            // 如果有平仓价格，更新订单收益
            if (position.getClosePrice() != null && position.getClosePrice().compareTo(BigDecimal.ZERO) > 0) {
                updateOrderIncome(orderInfo.getOrderNo());
            }

            return new ResultMessage(ResultMessage.SUCCEED_CODE, "仓位信息已接收");

        } catch (Exception e) {
            log.error("接收仓位信息失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "接收失败: " + e.getMessage());
        }
    }

    /**
     * 查询交易日志列表
     */

    public ResultMessage queryTradeLogList(String orderNo) {
        List<OrderTrade> tradeList = orderTradeMapper.selectByOrderNo(orderNo);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, tradeList);
    }

    /**
     * 查询仓位信息列表
     */

    public ResultMessage queryPositionList(String orderNo) {
        List<OrderPosition> positionList = orderPositionMapper.selectByOrderNo(orderNo);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, positionList);
    }

    /**
     * 更新订单收益
     */
    private void updateOrderIncome(String orderNo) {
        List<OrderTrade> tradeList = orderTradeMapper.selectByOrderNo(orderNo);
        BigDecimal totalIncome = BigDecimal.ZERO;
        for (OrderTrade trade : tradeList) {
            if (trade.getIncome() != null) {
                totalIncome = totalIncome.add(trade.getIncome());
            }
        }

        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        if (orderInfo != null) {
            orderInfo.setIncome(totalIncome);
            orderInfo.setUpdateTime(new Date());
            orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
        }
    }

=======
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
>>>>>>> origin/main
}
