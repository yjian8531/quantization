package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.*;
import com.example.core.mainbody.service.MainService;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderTradeMapper orderTradeMapper;

    @Autowired
    private OrderPositionMapper orderPositionMapper;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private ApikeyInfoMapper apikeyInfoMapper;

    @Autowired
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
    }


    /**
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

}
