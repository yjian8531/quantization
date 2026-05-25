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
import com.example.core.common.vo.product.ExchangeListVO;
import com.example.core.common.vo.product.SymbolListVO;
import com.example.core.common.vo.robot.HistoryPositionVO;
import com.example.core.common.vo.robot.RobotDetailVO;
import com.example.core.common.vo.robot.RobotListVO;
import com.example.core.common.vo.robot.TradeRecordVO;
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

    @Autowired
    private SymbolInfoMapper symbolInfoMapper;

    @Autowired
    private UserFinanceMapper userFinanceMapper;

    @Autowired
    private FinanceDetailMapper financeDetailMapper;

    @Autowired
    private StrategyInfoMapper strategyInfoMapper;

    @Autowired
    private OrderTaskMapper orderTaskMapper;


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

            // 2. 校验策略模板是否存在
            if (StringUtils.isEmpty(so.getStrategyInfoId())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板ID不能为空");
            }
            StrategyInfo strategyInfo = strategyInfoMapper.selectByStrategyId(so.getStrategyInfoId());
            if (strategyInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板不存在或已禁用");
            }

            // 3. 校验APIKey是否存在且属于当前用户
            ApikeyInfo apikeyInfo = apikeyInfoMapper.selectByIdAndUserId(so.getApikeyId(), userId);
            if (apikeyInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "APIKey不存在或不属于当前用户");
            }
            if (apikeyInfo.getStatus() != StrategyConstant.ConfigStatus.NORMAL) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "APIKey已被禁用");
            }

            // 4. 获取可用的主机配置
            MainConfig mainConfig = mainConfigMapper.selectNormalConfig();
            if (mainConfig == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "暂无可用的主机配置");
            }

            // 5. 创建云服务器
            String instanceId = mainService.create(mainConfig);
            if (StringUtils.isEmpty(instanceId)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "创建机器人服务器失败");
            }
            String mainNo = CommonUtil.getRandomStr(12);
            // 6. 创建主机信息记录（记录实例ID）
            MainInfo mainInfo = new MainInfo();
            mainInfo.setMainNo(mainNo);
            mainInfo.setConfigId(mainConfig.getId());
            mainInfo.setServiceNo(instanceId);
            mainInfo.setCreateTime(new Date());
            mainInfo.setUpdateTime(new Date());
            mainInfoMapper.insertSelective(mainInfo);

            // 7. 生成订单编号
            String orderNo = CommonUtil.getRandomStr(8);

            // 8. 创建策略订单
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
            orderInfo.setStrategyId(strategyInfo.getStrategyId());
            orderInfo.setStatus(StrategyConstant.OrderStatus.STARTING);
            orderInfo.setAnnualizedRate(product.getEstimateRate());
            orderInfo.setCreateTime(new Date());
            int i = orderInfoMapper.insertSelective(orderInfo);
            if(i > 0){

                /** 添加创建策略机器人任务 **/
                OrderTask orderTask = new OrderTask();
                orderTask.setOrderNo(orderInfo.getOrderNo());
                orderTask.setTag(0);
                orderTask.setStatus(0);
                orderTask.setCreateTime(new Date());
                orderTask.setUpdateTime(new Date());
                orderTaskMapper.insertSelective(orderTask);

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
     * 启动策略
     */
    @Transactional
    public ResultMessage startStrategyOrder(String orderNo) {
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());
        ApikeyInfo apikeyInfo = apikeyInfoMapper.selectByPrimaryKey(orderInfo.getApikeyId());

        try{
            String exchange = apikeyInfo.getFootplate() == 0 ? "binance" : "gateio";
            String apiKey = apikeyInfo.getApikey();
            String secret = apikeyInfo.getSecret();

            StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
            String str = strategyUtil.startStrategy(orderNo, exchange, orderInfo.getSymbol(), apiKey, secret, orderInfo.getParamStr());
            JSONObject result = JSONObject.fromObject(str);
            if("0000".equals(result.getString("code"))) {
                JSONObject data = result.getJSONObject("data");
                // 保存Python进程PID
                orderInfo.setPid(data.getString("pid"));
                orderInfo.setStatus(StrategyConstant.OrderStatus.RUNNING);
                orderInfo.setUpdateTime(new Date());
                orderInfoMapper.updateByPrimaryKeySelective(orderInfo);

                return new ResultMessage(ResultMessage.SUCCEED_CODE, "启动成功");

            }else{
                return new ResultMessage(ResultMessage.FAILED_CODE, "启动策略失败", result.getString("msg"));
            }
        }catch (Exception e){
            log.error("启动策略异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "启动策略异常", e.getMessage());
        }

    }

    /**
     * 停止策略
     * @param orderNo
     * @return
     */
    public ResultMessage stopStrategyOrder(String orderNo){
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());

        StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
        String str = strategyUtil.stopStrategy(orderNo);
        JSONObject result = JSONObject.fromObject(str);
        if("0000".equals(result.getString("code"))) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "停止成功");
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "停止策略失败", result.getString("msg"));
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

    // ==================== 策略模板 CRUD ====================

    /**
     * 新增策略模板
     */
    @Override
    public ResultMessage addStrategyInfo(StrategyInfo strategyInfo) {
        try {
            if (StringUtils.isEmpty(strategyInfo.getStrategyId())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略ID不能为空");
            }
            if (StringUtils.isEmpty(strategyInfo.getStrategyName())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略名称不能为空");
            }
            strategyInfo.setCreateTime(new Date());
            strategyInfo.setUpdateTime(new Date());
            int i = strategyInfoMapper.insertSelective(strategyInfo);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "新增策略模板成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "新增策略模板失败");
        } catch (Exception e) {
            log.error("新增策略模板失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "新增失败: " + e.getMessage());
        }
    }

    /**
     * 修改策略模板
     */
    @Override
    public ResultMessage updateStrategyInfo(StrategyInfo strategyInfo) {
        try {
            if (strategyInfo.getId() == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板ID不能为空");
            }
            StrategyInfo exist = strategyInfoMapper.selectByPrimaryKey(strategyInfo.getId());
            if (exist == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板不存在");
            }
            strategyInfo.setUpdateTime(new Date());
            int i = strategyInfoMapper.updateByPrimaryKeySelective(strategyInfo);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "修改策略模板成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "修改策略模板失败");
        } catch (Exception e) {
            log.error("修改策略模板失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "修改失败: " + e.getMessage());
        }
    }

    /**
     * 删除策略模板
     */
    @Override
    public ResultMessage deleteStrategyInfo(Integer id) {
        try {
            if (id == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板ID不能为空");
            }
            StrategyInfo exist = strategyInfoMapper.selectByPrimaryKey(id);
            if (exist == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板不存在");
            }
            int i = strategyInfoMapper.deleteByPrimaryKey(id);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除策略模板成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "删除策略模板失败");
        } catch (Exception e) {
            log.error("删除策略模板失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "删除失败: " + e.getMessage());
        }
    }

    /**
     * 根据策略ID查询策略模板详情
     */
    @Override
    public ResultMessage queryStrategyInfo(String strategyId) {
        try {
            if (StringUtils.isEmpty(strategyId)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略ID不能为空");
            }
            StrategyInfo strategyInfo = strategyInfoMapper.selectByStrategyId(strategyId);
            if (strategyInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略模板不存在");
            }
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, strategyInfo);
        } catch (Exception e) {
            log.error("查询策略模板失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "查询失败: " + e.getMessage());
        }
    }

    /**
     * 查询策略模板列表
     */
    @Override
    public ResultMessage queryStrategyInfoList() {
        try {
            List<StrategyInfo> list = strategyInfoMapper.selectAll();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("total", list.size());
            resultMap.put("list", list);
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
        } catch (Exception e) {
            log.error("查询策略模板列表失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "查询失败: " + e.getMessage());
        }
    }



}
