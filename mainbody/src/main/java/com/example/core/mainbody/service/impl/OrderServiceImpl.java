package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.*;
import com.example.core.mainbody.service.MainService;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.so.order.UpdateStrategySO;
import com.example.core.mainbody.so.order.UpdateStrategyTagSO;
import com.example.core.mainbody.so.strategy.CreateStrategyOrderSO;
import com.example.core.mainbody.so.strategy.PositionPushSO;
import com.example.core.mainbody.so.strategy.TradeLogPushSO;
import com.example.core.mainbody.utils.MartinRiskAnalyzer;
import com.example.core.mainbody.utils.PythonExecutor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.example.core.common.vo.product.ExchangeListVO;
import com.example.core.common.vo.product.ParamConfigGroupVO;
import com.example.core.common.vo.product.ParamConfigItemVO;
import com.example.core.common.vo.product.SymbolListVO;
import com.example.core.common.vo.robot.HistoryPositionVO;
import com.example.core.common.vo.robot.ProfitRecordItem;
import com.example.core.common.vo.robot.RobotDetailVO;
import com.example.core.common.vo.robot.RobotListVO;
import com.example.core.common.vo.robot.TradeRecordVO;
import com.example.core.mainbody.so.robot.QueryHistoryPositionSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
import com.example.core.mainbody.so.robot.QueryTradeRecordSO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private ProductParamMapper productParamMapper;

    @Value("${aes.encryptkey}")
    private String ENCRYPT_KEY;

    /**
     * 策略参数风控评估
     * @param paramStr
     * @return
     */
    public ResultMessage  checkStrategyParam(String paramStr){
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG);
        /*MartinRiskAnalyzer.AnalysisResult result = MartinRiskAnalyzer.analyze(paramStr);
        if(!result.canAddAll){
            return new ResultMessage(ResultMessage.FAILED_CODE, "初始开仓金额或加仓倍数过高，风险评估不通过。");
        }else if(result.fullAddPriceRisePercent < 40){
            return new ResultMessage(ResultMessage.FAILED_CODE, "整体加仓比率覆盖的涨幅小于40%，风险评估不通过。");
        }else if(result.maxRisePercent < 50){
            return new ResultMessage(ResultMessage.FAILED_CODE, "最低强平价位覆盖的涨幅小于50%，风险评估不通过。");
        }else if(result.maxRisePercent < result.fullAddPriceRisePercent){
            return new ResultMessage(ResultMessage.FAILED_CODE, "最低强平价位覆盖的涨幅小于整体加仓比率覆盖的涨幅，风险评估不通过。");
        }else{
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG);
        }*/
    }


    /**
     * 创建策略订单
     */
    @Transactional
    public ResultMessage createStrategyOrder(CreateStrategyOrderSO so, String userId) {
        try {

            JSONObject json = JSONObject.fromObject(so.getParamStr());
            JSONObject amountObj = json.getJSONObject("amountObj");
            if(amountObj.get("TotalAmount") == null){
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略参数异常,缺少本金");
            }

            if(amountObj.get("FirstOrderAmount") == null){
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略参数异常,缺少初始开仓金额");
            }else{
                if(new BigDecimal(amountObj.getString("FirstOrderAmount")).compareTo(BigDecimal.valueOf(25)) < 0){
                    return new ResultMessage(ResultMessage.FAILED_CODE, "初始开仓金额不能小于25");
                }
            }

            /** 策略参数风控评估 **/
            ResultMessage r = checkStrategyParam(so.getParamStr());
            if(r.getCode().equals(ResultMessage.FAILED_CODE)){
                return r;
            }

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



            /** 计算 月租金 = 固定月租 + 本金 * 固定百分比 **/
            BigDecimal totalAmount = new BigDecimal(amountObj.getString("TotalAmount"));
            BigDecimal monthlyAmount = product.getMonthlyFee().add(totalAmount.multiply(product.getMonthlyRatio().multiply(BigDecimal.valueOf(0.01))));


            /** 校验交易所余额 **/
            Map<String, BigDecimal> result = null;
            // 使用对称加密加密 apiKey 和 secret
            String decryptApiKey = AESUtil.Decrypt(apikeyInfo.getApikey(), ENCRYPT_KEY);
            String decryptSecret = AESUtil.Decrypt(apikeyInfo.getSecret(), ENCRYPT_KEY);
            if(apikeyInfo.getFootplate() == 0){
                BinanceApi binanceApi = new BinanceApi(decryptApiKey,decryptSecret);
                result = binanceApi.getBalanceContract();
            }else if(apikeyInfo.getFootplate() == 1){
                GateioApi gateioApi = new GateioApi(decryptApiKey,decryptSecret);
                result = gateioApi.getFuturesBalance("usdt");
            }
            if(result == null){
                return new ResultMessage(ResultMessage.FAILED_CODE, "API Key或 Secret，已失效");
            }else{
                BigDecimal accountBalance = result.get("USDT");
                /*if(amountObj.get("Lever") != null){
                    accountBalance = accountBalance.multiply(BigDecimal.valueOf(amountObj.getInt("Lever")));
                }*/
                if(accountBalance.compareTo(totalAmount) < 0){
                    return new ResultMessage(ResultMessage.FAILED_CODE, "交易所余额不足。");
                }
            }

            //冻结金额
            UserFinance finance = userFinanceMapper.selectByUserId(userId);
            if(finance.getValidNum().compareTo(monthlyAmount) < 0){
                return new ResultMessage(ResultMessage.FAILED_CODE, "余额不足，请先充值。");
            }
            userFinanceMapper.updateBalanceByUserId(userId,"seal",monthlyAmount);

            // 5. 创建云服务器
            String instanceId = mainService.create(mainConfig);
            if (StringUtils.isEmpty(instanceId)) {
                //解冻金额
                userFinanceMapper.updateBalanceByUserId(userId,"seal",product.getMonthlyFee());
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
            orderInfo.setTotalAmount(totalAmount);
            orderInfo.setIncome(BigDecimal.valueOf(0));
            orderInfo.setIncomeRate(BigDecimal.valueOf(0));
            orderInfo.setAnnualizedRate(BigDecimal.valueOf(0));
            orderInfo.setStrategyId(strategyInfo.getStrategyId());
            orderInfo.setStatus(StrategyConstant.OrderStatus.STARTING);
            orderInfo.setPub(0);
            orderInfo.setTag(0);
            orderInfo.setCreateTime(new Date());
            orderInfo.setEntTime(DateUtil.addDateMonths(new Date(),1));
            orderInfo.setUpdateTime(new Date());
            int i = orderInfoMapper.insertSelective(orderInfo);
            if(i > 0){

                FinanceDetail financeDetail = new FinanceDetail();
                financeDetail.setUserId(userId);
                financeDetail.setFinanceNo(CommonUtil.getRandomStr(8));
                financeDetail.setOrderNo(orderNo);
                financeDetail.setType(1);//消费类型
                financeDetail.setCoinType("USDT");
                financeDetail.setMoneyNum(monthlyAmount);
                financeDetail.setPeriod(1);//周期(月)
                financeDetail.setTag("buy");
                financeDetail.setDirection(1);//支出
                financeDetail.setWay(0);
                financeDetail.setStatus(0);//进行中
                financeDetail.setCreateTime(new Date());
                financeDetail.setUpdateTime(new Date());
                financeDetailMapper.insertSelective(financeDetail);


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
     * 根据策略订单编号查询参数
     * @param orderNo 策略订单号
     * @return
     */
    @Override
    public ResultMessage queryStrategyParam(String orderNo) {
        try {
            if (StringUtils.isEmpty(orderNo)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单编号不能为空");
            }
            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
            }
            if (StringUtils.isEmpty(orderInfo.getParamStr())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "策略参数为空");
            }

            // 查询产品参数模板（获取参数的分组、名称、key、单位、描述等元数据）
            List<ProductParam> paramList = productParamMapper.selectByProductIdAndStrategyId(
                    orderInfo.getProductId(), orderInfo.getStrategyId());
            if (paramList == null || paramList.isEmpty()) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, new ArrayList<>());
            }

            // 解析 paramStr JSON，提取实际参数值
            JSONObject json = JSONObject.fromObject(orderInfo.getParamStr());
            JSONObject amountObj = json.getJSONObject("amountObj");
            JSONArray positionArr = json.getJSONArray("position");

            // 构建 amountObj 的 Key-Value 查找表
            Map<String, String> amountValueMap = new HashMap<>();
            if (amountObj != null) {
                for (Object k : amountObj.keySet()) {
                    String key = (String) k;
                    Object val = amountObj.get(key);
                    amountValueMap.put(key, val != null ? String.valueOf(val) : "");
                }
            }

            // position 数组索引计数器，按 paramKey 名称分别计数，实现顺序匹配
            int ratioIndex = 0;
            int multipleIndex = 0;

            // 按 paramGroup 分组
            Map<String, List<ProductParam>> groupMap = new LinkedHashMap<>();
            for (ProductParam param : paramList) {
                groupMap.computeIfAbsent(param.getParamGroup(), k -> new ArrayList<>()).add(param);
            }

            // 构造返回数据
            List<ParamConfigGroupVO> resultList = new ArrayList<>();
            for (Map.Entry<String, List<ProductParam>> entry : groupMap.entrySet()) {
                ParamConfigGroupVO groupVO = new ParamConfigGroupVO();
                groupVO.setName(entry.getKey());

                List<ParamConfigItemVO> configList = new ArrayList<>();
                for (ProductParam param : entry.getValue()) {
                    ParamConfigItemVO itemVO = new ParamConfigItemVO();
                    itemVO.setName(param.getParamName());
                    itemVO.setKey(param.getParamKey());
                    itemVO.setUnit(param.getUnit());
                    itemVO.setDescribe(param.getDescribe());

                    // 优先从 amountObj 中匹配值
                    String value = amountValueMap.get(param.getParamKey());
                    if (value == null && positionArr != null && positionArr.size() > 0) {
                        // amountObj 未匹配到，尝试从 position 数组中按顺序匹配
                        if ("ratio".equals(param.getParamKey()) && ratioIndex < positionArr.size()) {
                            JSONObject pos = positionArr.getJSONObject(ratioIndex);
                            value = String.valueOf(pos.get("ratio"));
                            ratioIndex++;
                        } else if ("multiple".equals(param.getParamKey()) && multipleIndex < positionArr.size()) {
                            JSONObject pos = positionArr.getJSONObject(multipleIndex);
                            value = String.valueOf(pos.get("multiple"));
                            multipleIndex++;
                        }
                    }
                    // 兜底：使用数据库中的默认值
                    if (value == null) {
                        value = param.getParamValue();
                    }

                    itemVO.setValue(value);
                    configList.add(itemVO);
                }
                groupVO.setConfig(configList);
                resultList.add(groupVO);
            }

            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultList);
        } catch (Exception e) {
            log.error("查询策略参数失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "查询失败: " + e.getMessage());
        }
    }


    /**
     * 更新策略参数
     * @param orderNo 策略订单号
     * @param paramStr 参数字符串
     * @return
     */
    @Override
    public ResultMessage updateStrategyParam(String orderNo, String paramStr) {
        try {
            if (StringUtils.isEmpty(orderNo)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单编号不能为空");
            }
            if (StringUtils.isEmpty(paramStr)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "参数不能为空");
            }

            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
            }

            // 不是停止运行状态下的机器人不允许更新参数
            if (orderInfo.getStatus() == StrategyConstant.OrderStatus.RUNNING) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "请先关闭机器人");
            }else if(orderInfo.getStatus() != StrategyConstant.OrderStatus.PAUSED){
                return new ResultMessage(ResultMessage.FAILED_CODE, "当前状态不支持此操作");
            }

            /** 策略参数风控评估 **/
            ResultMessage r = checkStrategyParam(paramStr);
            if (r.getCode().equals(ResultMessage.FAILED_CODE)) {
                return r;
            }

            JSONObject amountObj = JSONObject.fromObject(paramStr).getJSONObject("amountObj");
            /** 总投入 **/
            BigDecimal totalAmount = new BigDecimal(amountObj.getString("TotalAmount"));
            if(totalAmount.compareTo(orderInfo.getTotalAmount()) > 0){
                ApikeyInfo apikeyInfo = apikeyInfoMapper.selectByPrimaryKey(orderInfo.getApikeyId());
                /** 校验交易所余额 **/
                Map<String, BigDecimal> result = null;
                // 使用对称加密加密 apiKey 和 secret
                String decryptApiKey = AESUtil.Decrypt(apikeyInfo.getApikey(), ENCRYPT_KEY);
                String decryptSecret = AESUtil.Decrypt(apikeyInfo.getSecret(), ENCRYPT_KEY);
                if(apikeyInfo.getFootplate() == 0){
                    BinanceApi binanceApi = new BinanceApi(decryptApiKey,decryptSecret);
                    result = binanceApi.getBalanceContract();
                }else if(apikeyInfo.getFootplate() == 1){
                    GateioApi gateioApi = new GateioApi(decryptApiKey,decryptSecret);
                    result = gateioApi.getFuturesBalance("usdt");
                }
                if(result == null){
                    return new ResultMessage(ResultMessage.FAILED_CODE, "API Key或 Secret，已失效");
                }else{
                    /** 获取策略当前仓位价值 **/
                    MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());
                    String url = "http://"+mainInfo.getConnectIp()+":8699/api/position";
                    String boby = HttpRequest.sendGet(url);
                    JSONObject bobyJson = JSONObject.fromObject(boby);
                    JSONObject position = bobyJson.getJSONObject("data").getJSONObject("position");
                    /** 仓位数量 **/
                    BigDecimal amount = new BigDecimal(position.getString("Amount"));
                    /** 仓位价格 **/
                    BigDecimal price = new BigDecimal(position.getString("Price"));

                    BigDecimal accountBalance = result.get("USDT");
                    accountBalance = accountBalance.add(amount.multiply(price));
                    if(accountBalance.compareTo(totalAmount) < 0){
                        return new ResultMessage(ResultMessage.FAILED_CODE, "交易所余额不足。");
                    }
                }
                orderInfo.setTotalAmount(totalAmount);
            }


            // 更新参数
            orderInfo.setParamStr(paramStr);
            orderInfo.setUpdateTime(new Date());
            int i = orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
            if (i <= 0) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "参数更新失败");
            }
            
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "参数更新成功");
        } catch (Exception e) {
            log.error("更新策略参数失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 查询公开机器人列表
     * 返回：订单名称、用户名称、创建时间、运行天数、总收益、年化率、币对、收益记录数组
     * 收益记录聚合规则：运行>2个月按月汇总，≤2个月按天汇总
     */
    @Override
    public ResultMessage queryPublicRobotList(Integer exchange, Integer pageNum, Integer pageSize, String sortType) {
        // 设置默认分页参数
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        // 默认排序：创建时间倒序
        if (sortType == null || sortType.isEmpty()) {
            sortType = "createTime";
        }
        int start = (pageNum - 1) * pageSize;
        // 先查总数
        int total = orderInfoMapper.selectPublicRobotCount(exchange);
        // 分页查询列表
        List<RobotListVO> list = orderInfoMapper.selectPublicRobotList(exchange, start, pageSize, sortType);
        if (list == null) {
            list = new ArrayList<>();
        }
        // 为每个机器人填充收益记录数组
        for (RobotListVO vo : list) {
            try {
                int runDays = vo.getRunDays() != null ? vo.getRunDays() : 0;
                String dimension = runDays > 60 ? "month" : "day";
                List<ProfitRecordItem> profitRecords = orderPositionMapper.selectAggregatedProfit(vo.getOrderNo(), dimension);
                vo.setProfitRecords(profitRecords != null ? profitRecords : new ArrayList<>());
            } catch (Exception e) {
                log.error("查询机器人{}收益记录失败", vo.getOrderNo(), e);
                vo.setProfitRecords(new ArrayList<>());
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", total);
        resultMap.put("list", list);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 用户设置机器人公开或不公开
     */
    @Override
    public ResultMessage setRobotPublic(String orderNo, Integer pub, String userId) {
        try {
            if (orderNo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单编号不能为空");
            }
            if (pub == null || (pub != 0 && pub != 1)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "公开状态无效(0:不公开,1:公开)");
            }
            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
            }
            if (!orderInfo.getUserId().equals(userId)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "无权操作此订单");
            }
            OrderInfo updateInfo = new OrderInfo();
            updateInfo.setId(orderInfo.getId());
            updateInfo.setPub(pub);
            updateInfo.setUpdateTime(new Date());
            int i = orderInfoMapper.updateByPrimaryKeySelective(updateInfo);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "设置成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "设置失败");
        } catch (Exception e) {
            log.error("设置机器人公开状态失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "设置失败: " + e.getMessage());
        }
    }


    /**
     * 查询用户机器人列表
     * 返回：订单名称、用户名称、创建时间、运行天数、总收益、年化率、币对、收益记录数组
     * 收益记录聚合规则：运行>2个月按月汇总，≤2个月按天汇总
     */
    @Override
    public ResultMessage queryRobotList(String userId, QueryRobotSO so) {
        // 设置默认分页参数
        Integer pageNum = so.getPageNum();
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        Integer pageSize = so.getPageSize();
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        // 默认排序：创建时间倒序
        String sortType = so.getSortType();
        if (sortType == null || sortType.isEmpty()) {
            sortType = "createTime";
        }
        int start = (pageNum - 1) * pageSize;
        // 先查总数
        int total = orderInfoMapper.selectUserRobotCount(userId, so.getExchange());
        // 分页查询列表
        List<RobotListVO> list = orderInfoMapper.selectUserRobotList(userId, so.getExchange(), start, pageSize, sortType);
        if (list == null) {
            list = new ArrayList<>();
        }
        // 为每个机器人填充收益记录数组
        for (RobotListVO vo : list) {
            try {
                int runDays = vo.getRunDays() != null ? vo.getRunDays() : 0;
                String dimension = runDays > 60 ? "month" : "day";
                List<ProfitRecordItem> profitRecords = orderPositionMapper.selectAggregatedProfit(vo.getOrderNo(), dimension);
                vo.setProfitRecords(profitRecords != null ? profitRecords : new ArrayList<>());
            } catch (Exception e) {
                log.error("查询机器人{}收益记录失败", vo.getOrderNo(), e);
                vo.setProfitRecords(new ArrayList<>());
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", total);
        resultMap.put("list", list);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 查询用户机器人详情
     */
    @Override
    public ResultMessage queryRobotDetail(String userId, String orderNo) {
        RobotDetailVO detail = orderInfoMapper.selectRobotDetail(orderNo, userId);
        if (detail == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在或无权限查看");
        }

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detail);
    }

    /**
     * 查询公开机器人详情
     */
    @Override
    public ResultMessage queryPublicRobotDetail(String orderNo) {
        com.example.core.common.vo.robot.PublicRobotDetailVO detail = orderInfoMapper.selectPublicRobotDetail(orderNo);
        if (detail == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在或未公开");
        }

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detail);
    }

    /**
     * 查询用户历史持仓列表
     */
    @Override
    public ResultMessage queryHistoryPositionList(QueryHistoryPositionSO so) {
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(so.getOrderNo());
        if (orderInfo == null ) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
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
    public ResultMessage queryTradeRecordList(QueryTradeRecordSO so) {
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(so.getOrderNo());
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
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

            /** 上传策略文件 **/
            StrategyInfo strategyInfo = strategyInfoMapper.selectByStrategyId(orderInfo.getStrategyId());
            Boolean bl = PythonExecutor.exec(mainInfo.getConnectIp(),22,"root",mainInfo.getConnectPwd(),strategyInfo.getContent());
            if(!bl){
                return new ResultMessage(ResultMessage.FAILED_CODE, "启动策略失败", "策略文件上传不成功！");
            }

            String exchange = apikeyInfo.getFootplate() == 0 ? "binance" : "gateio";
            String apiKey = apikeyInfo.getApikey();
            String secret = apikeyInfo.getSecret();

            StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
            String str = strategyUtil.startStrategy(orderNo, exchange, orderInfo.getSymbol(), apiKey, secret,0, orderInfo.getParamStr());
            JSONObject result = JSONObject.fromObject(str);
            if("0000".equals(result.getString("code"))) {
                JSONObject data = result.getJSONObject("data");
                // 保存Python进程PID
                orderInfo.setPid(data.getString("pid"));
                orderInfo.setStatus(StrategyConstant.OrderStatus.RUNNING);
                orderInfo.setTag(0);//启动后默认进行循环状态
                orderInfo.setUpdateTime(new Date());
                orderInfoMapper.updateByPrimaryKeySelective(orderInfo);

                return new ResultMessage(ResultMessage.SUCCEED_CODE, "操作成功");

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
            // 更新订单状态为已结束
            orderInfo.setStatus(StrategyConstant.OrderStatus.PAUSED);
            orderInfo.setEntTime(new Date());
            orderInfo.setUpdateTime(new Date());
            orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "停止成功");
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "停止策略失败", result.getString("msg"));
        }
    }

    /**
     * 重启策略
     * @param orderNo
     * @return
     */
    public ResultMessage restartStrategyOrder(String orderNo){
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());

        StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
        String str = strategyUtil.stopStrategy(orderNo);
        JSONObject result = JSONObject.fromObject(str);
        if("0000".equals(result.getString("code"))) {
            ResultMessage r = startStrategyOrder(orderNo);
            return r;
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "无法停止，重启失败！");
        }
    }


    /**
     * 更新策略平仓
     * @param updateStrategyTagSO
     * @return
     */
    public ResultMessage updateStrategyTag(UpdateStrategyTagSO updateStrategyTagSO){
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(updateStrategyTagSO.getOrderNo());
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());

        StrategyUtil strategyUtil = new StrategyUtil(mainInfo.getConnectIp());
        String str = strategyUtil.updateStrategyTag(updateStrategyTagSO.getTag());
        JSONObject result = JSONObject.fromObject(str);
        if("0000".equals(result.getString("code"))) {

            OrderInfo entity = new OrderInfo();
            entity.setId(orderInfo.getId());
            entity.setTag(updateStrategyTagSO.getTag());
            entity.setUpdateTime(new Date());
            int i =  orderInfoMapper.updateByPrimaryKeySelective(entity);
            if(i > 0){
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "操作成功");
            }else{
                return new ResultMessage(ResultMessage.FAILED_CODE, ResultMessage.FAILED_MSG);
            }
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "操作失败");
        }
    }


    /**
     * 更换策略和币对
     * @param updateStrategySO
     * @return
     */
    public ResultMessage updateStrategy(UpdateStrategySO updateStrategySO){
        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(updateStrategySO.getOrderNo());
        if (orderInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
        }

        /** 获取策略当前仓位价值 **/
        MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());
        String url = "http://"+mainInfo.getConnectIp()+":8699/api/position";
        String boby = HttpRequest.sendGet(url);
        JSONObject bobyJson = JSONObject.fromObject(boby);
        String position = bobyJson.getJSONObject("data").getString("position");
        if(position != null && !"null".equals(position)){
            return new ResultMessage(ResultMessage.FAILED_CODE, "请先平掉仓位");
        }

        // 不是停止运行状态下的机器人不允许更新参数
        if (orderInfo.getStatus() == StrategyConstant.OrderStatus.RUNNING) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "请先关闭机器人");
        }else if(orderInfo.getStatus() != StrategyConstant.OrderStatus.PAUSED){
            return new ResultMessage(ResultMessage.FAILED_CODE, "当前状态不支持此操作");
        }

        OrderInfo entity = new OrderInfo();
        entity.setId(orderInfo.getId());

        if(updateStrategySO.getStrategyId() != null){
            entity.setStrategyId(updateStrategySO.getStrategyId());
        }
        if(updateStrategySO.getSymbol() != null){
            entity.setSymbol(updateStrategySO.getSymbol());
        }
        entity.setUpdateTime(new Date());
        int i =  orderInfoMapper.updateByPrimaryKeySelective(entity);
        if(i > 0){
            ResultMessage r = startStrategyOrder(updateStrategySO.getOrderNo());
            return r;
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, ResultMessage.FAILED_MSG);
        }
    }


    /**
     * 接收交易日志推送
     */
    @Transactional
    public ResultMessage receiveTradeLog(TradeLogPushSO tradeLog) {
        try {
            // 校验订单是否存在
            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(tradeLog.getOrderNo());
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
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
            orderTrade.setCreateTime(DateUtil.fomatDate(tradeLog.getCreateTime()));
            orderTrade.setUpdateTime(new Date());
            orderTradeMapper.insertSelective(orderTrade);
            if(tradeLog.getTradeNum() != null){
                // 更新订单收益
                updateOrderIncome(orderInfo.getOrderNo());
                /** 更新产品的累计交易额 **/
                orderProductMapper.updateTotalAmount(orderInfo.getProductId());
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
            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(position.getOrderNo());
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
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
            orderPosition.setStartTime(DateUtil.fomatDate(position.getStartTime()));
            orderPosition.setEndTime(DateUtil.fomatDate(position.getEndTime()));
            orderPositionMapper.insertSelective(orderPosition);

            // 如果有平仓价格，更新订单收益
            if (position.getClosePrice() != null && position.getClosePrice().compareTo(BigDecimal.ZERO) > 0) {
                updateOrderIncome(orderInfo.getOrderNo());

                /** 更新产品的预估年化率 **/
                orderProductMapper.updateEstimateRate(orderInfo.getProductId());
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
     * 更新订单收益、收益率及年化率
     */
    public void updateOrderIncome(String orderNo) {
        List<OrderPosition> positionList = orderPositionMapper.selectByOrderNo(orderNo);
        BigDecimal totalIncome = BigDecimal.ZERO;
        for (OrderPosition position : positionList) {
            if (position.getIncome() != null) {
                totalIncome = totalIncome.add(position.getIncome());
            }
        }

        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
        if (orderInfo != null) {
            orderInfo.setIncome(totalIncome);

            // 计算收益率和年化率
            BigDecimal totalAmount = orderInfo.getTotalAmount();
            if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 收益率 = 总收益 / 投入本金 * 100
                BigDecimal incomeRate = totalIncome.divide(totalAmount, 6, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
                orderInfo.setIncomeRate(incomeRate);

                // 计算运行天数
                Date startTime = orderInfo.getCreateTime();
                Date endTime = new Date();
                if (startTime != null) {
                    long diffMillis = endTime.getTime() - startTime.getTime();
                    long runDays = diffMillis / (1000L * 60 * 60 * 24);
                    if (runDays > 0) {
                        // 年化率 = 收益率 / 运行天数 * 365
                        BigDecimal annualizedRate = incomeRate.divide(new BigDecimal(runDays), 6, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal("365"));
                        orderInfo.setAnnualizedRate(annualizedRate);
                    }
                }
            }

            orderInfo.setUpdateTime(new Date());
            orderInfoMapper.updateByPrimaryKeySelective(orderInfo);
        }
    }

    /**
     * 添加用户交易所API
     */
    @Override
    public ResultMessage addApikeyInfo(com.example.core.mainbody.so.AddApikeySO so, String userId) {
        try {
            if (StringUtils.isEmpty(so.getApikey())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "API Key不能为空");
            }
            if (StringUtils.isEmpty(so.getSecret())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "Secret不能为空");
            }
            if (StringUtils.isEmpty(so.getName())) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "名称不能为空");
            }

            /** 交易交易所余额 **/
            Map<String, BigDecimal> result = null;
            if(so.getFootplate() == 0){
                BinanceApi binanceApi = new BinanceApi(so.getApikey(),so.getSecret());
                result = binanceApi.getBalanceContract();
            }else if(so.getFootplate() == 1){
                GateioApi gateioApi = new GateioApi(so.getApikey(),so.getSecret());
                result = gateioApi.getFuturesBalance("usdt");
            }

            if(result == null){
                return new ResultMessage(ResultMessage.FAILED_CODE, "无效的API Key或 Secret");
            }


            // 使用对称加密加密 apiKey 和 secret
            String encryptedApiKey = AESUtil.Encrypt(so.getApikey(), ENCRYPT_KEY);
            String encryptedSecret = AESUtil.Encrypt(so.getSecret(), ENCRYPT_KEY);

            // 校验Apikey唯一性
            ApikeyInfo existByKey = apikeyInfoMapper.selectByApikey(encryptedApiKey);
            if (existByKey != null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "该API Key已存在，不允许重复添加");
            }
            ApikeyInfo apikeyInfo = new ApikeyInfo();
            apikeyInfo.setUserId(userId);
            apikeyInfo.setFootplate(so.getFootplate());
            apikeyInfo.setName(so.getName());
            apikeyInfo.setApikey(encryptedApiKey);
            apikeyInfo.setSecret(encryptedSecret);
            apikeyInfo.setType(0);
            apikeyInfo.setStatus(0);
            apikeyInfo.setCreateTime(new Date());
            apikeyInfo.setUpdateTime(new Date());
            int i = apikeyInfoMapper.insertSelective(apikeyInfo);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "添加成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "添加失败");
        } catch (Exception e) {
            log.error("添加用户交易所API失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户交易所API
     */
    @Override
    public ResultMessage updateApikeyInfo(ApikeyInfo apikeyInfo, String userId) {
        try {
            if (apikeyInfo.getId() == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "API ID不能为空");
            }
            ApikeyInfo exist = apikeyInfoMapper.selectByIdAndUserId(apikeyInfo.getId(), userId);
            if (exist == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "API Key不存在或不属于当前用户");
            }
            apikeyInfo.setUpdateTime(new Date());
            int i = apikeyInfoMapper.updateByPrimaryKeySelective(apikeyInfo);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "更新成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "更新失败");
        } catch (Exception e) {
            log.error("更新用户交易所API失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户交易所API
     */
    @Override
    public ResultMessage deleteApikeyInfo(Integer id, String userId) {
        try {
            if (id == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "API ID不能为空");
            }
            ApikeyInfo exist = apikeyInfoMapper.selectByIdAndUserId(id, userId);
            if (exist == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "API Key不存在或不属于当前用户");
            }
            exist.setStatus(1);//无效状态
            exist.setUpdateTime(new Date());
            int i = apikeyInfoMapper.updateByPrimaryKeySelective(exist);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
            }
            return new ResultMessage(ResultMessage.FAILED_CODE, "删除失败");
        } catch (Exception e) {
            log.error("删除用户交易所API失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "删除失败: " + e.getMessage());
        }
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

    /**
     * 接收策略状态心跳上报
     * Python策略定期上报运行状态，Java端同步更新订单信息
     */
    @Override
    public ResultMessage receiveStrategyStatus(String statusJson) {
        try {
            if (StringUtils.isEmpty(statusJson)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "上报数据为空");
            }

            JSONObject statusData = JSONObject.fromObject(statusJson);
            String orderNo = statusData.optString("orderNo");
            String status = statusData.optString("status");


            // 查询订单
            OrderInfo orderInfo = null;
            if (StringUtils.isNotEmpty(orderNo)) {
                orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
            }
            if (orderInfo == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "订单不存在");
            }

            // 同步状态
            if ("running".equals(status)) {
                if (orderInfo.getStatus() == StrategyConstant.OrderStatus.ENDED) {
                    orderInfo.setRemark("策略订单已结束，但是机器人状态是[运行中]");
                }else if (orderInfo.getStatus() == StrategyConstant.OrderStatus.PAUSED || orderInfo.getStatus() == StrategyConstant.OrderStatus.STARTING) {
                    orderInfo.setStatus(StrategyConstant.OrderStatus.RUNNING);
                }
            }else if("close".equals(status)){
                if (orderInfo.getStatus() == StrategyConstant.OrderStatus.ENDED) {
                    orderInfo.setRemark("策略订单已结束，但是机器人状态是[暂停]");
                }else {
                    orderInfo.setStatus(StrategyConstant.OrderStatus.PAUSED);
                }
            }
            orderInfo.setUpdateTime(new Date());
            orderInfoMapper.updateByPrimaryKeySelective(orderInfo);

            log.info("策略状态同步完成: orderNo={}, status={}, profit={}", orderNo, status);
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "状态已同步");

        } catch (Exception e) {
            log.error("接收策略状态上报异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "状态同步失败: " + e.getMessage());
        }
    }

    /**
     * 查询收益曲线数据
     * 从 w_order_position 历史仓位中按时间顺序计算累计收益
     */
    @Override
    public ResultMessage queryProfitCurve(String orderNo) {
        try {
            OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);

            Date createTime = orderInfo.getCreateTime();
            if (createTime == null) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "暂无收益数据", new ArrayList<>());
            }

            // 计算运行天数，决定时间聚合维度
            long diffMillis = System.currentTimeMillis() - createTime.getTime();
            long runDays = diffMillis / (1000L * 60 * 60 * 24);
            String dimension = runDays > 60 ? "month" : "day";

            // 查询已聚合的收益数据
            List<ProfitRecordItem> dataList = orderPositionMapper.selectAggregatedProfit(orderNo, dimension);
            Map<String, BigDecimal> incomeMap = new HashMap<>();
            if (dataList != null) {
                for (ProfitRecordItem item : dataList) {
                    incomeMap.put(item.getTimeLabel(), item.getIncome() != null ? item.getIncome() : BigDecimal.ZERO);
                }
            }

            // 生成完整的时间节点序列（从创建时间至今），缺失节点补0
            List<ProfitRecordItem> result = new ArrayList<>();
            if (dimension.equals("month")) {
                result = generateMonthlyCurve(createTime, incomeMap);
            } else {
                result = generateDailyCurve(createTime, incomeMap);
            }

            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, result);

        } catch (Exception e) {
            log.error("查询收益曲线失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "查询失败: " + e.getMessage());
        }
    }

    /** 生成按月聚合的收益曲线（缺失月份补0） */
    private List<ProfitRecordItem> generateMonthlyCurve(Date createTime, Map<String, BigDecimal> incomeMap) {
        List<ProfitRecordItem> result = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(createTime);
        // 起始年月
        int startYear = cal.get(java.util.Calendar.YEAR);
        int startMonth = cal.get(java.util.Calendar.MONTH) + 1;
        // 当前年月
        cal.setTime(new Date());
        int endYear = cal.get(java.util.Calendar.YEAR);
        int endMonth = cal.get(java.util.Calendar.MONTH) + 1;

        for (int y = startYear, m = startMonth; y < endYear || (y == endYear && m <= endMonth); ) {
            String label = String.format("%d-%02d", y, m);
            ProfitRecordItem item = new ProfitRecordItem();
            item.setTimeLabel(label);
            item.setIncome(incomeMap.getOrDefault(label, BigDecimal.ZERO));
            result.add(item);
            m++;
            if (m > 12) {
                m = 1;
                y++;
            }
        }
        return result;
    }

    /** 生成按天聚合的收益曲线（缺失天数补0） */
    private List<ProfitRecordItem> generateDailyCurve(Date createTime, Map<String, BigDecimal> incomeMap) {
        List<ProfitRecordItem> result = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(createTime);
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.set(java.util.Calendar.MINUTE, 0);
        today.set(java.util.Calendar.SECOND, 0);
        today.set(java.util.Calendar.MILLISECOND, 0);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        while (!cal.after(today)) {
            String label = sdf.format(cal.getTime());
            ProfitRecordItem item = new ProfitRecordItem();
            item.setTimeLabel(label);
            item.setIncome(incomeMap.getOrDefault(label, BigDecimal.ZERO));
            result.add(item);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }


}
