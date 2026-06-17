package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.FinanceDetail;
import com.example.core.common.entity.OrderInfo;
import com.example.core.common.entity.OrderProduct;
import com.example.core.common.entity.ProductParam;
import com.example.core.common.entity.StrategyInfo;
import com.example.core.common.entity.UserFinance;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.vo.product.*;
import com.example.core.common.vo.robot.RobotListVO;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.ConfigRobotSO;
import com.example.core.mainbody.so.product.QueryProductSO;
import com.example.core.mainbody.so.product.QueryStrategyByExchangeSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
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
public class ProductServiceImpl implements ProductService {

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
    private StrategyInfoMapper strategyInfoMapper;

    @Autowired
    private OrderTradeMapper orderTradeMapper;

    @Autowired
    private ProductParamMapper productParamMapper;

    /**
     * 查询产品列表
     * 对应原型图：产品精选列表页
     * 功能说明：
     * 1. 根据前端传入的 level 参数筛选产品等级（全部/入门/标准/高级）
     * 2. 只返回 status=0（正常）的产品
     * 3. 返回产品核心展示字段：名称、预估年化率、月租、累计交易额、盈利占比
     * 4. level 字段直接返回数字（1/2/3），由前端自行转换为标签颜色
     */
    @Override
    public ResultMessage queryProductList(QueryProductSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());

        Page<ProductListVO> page = (Page<ProductListVO>) orderProductMapper.selectProductList(so.getLevel());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 查询产品详情
     * 对应原型图：产品详情弹窗页
     * 功能说明：
     * 1. 查询产品基础信息（名称、等级、预估年化率、月租、说明文案、累计盈利、投入限制）
     */
    @Override
    public ResultMessage queryProductDetail(Integer productId) {
        OrderProduct product = orderProductMapper.selectByPrimaryKey(productId);
        if (product == null || product.getStatus() != 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "产品不存在或已禁用");
        }

        ProductDetailVO detailVO = new ProductDetailVO();
        detailVO.setId(product.getId());
        detailVO.setProductName(product.getProductName());
        detailVO.setLevel(product.getLevel());
        detailVO.setEstimateRate(product.getEstimateRate());
        detailVO.setMonthlyFee(product.getMonthlyFee());
        detailVO.setMonthlyRatio(product.getMonthlyRatio());
        detailVO.setDescription(product.getDescription());
        detailVO.setCumulativeProfit(product.getCumulativeProfit());
        detailVO.setTopLimit(product.getTopLimit());
        detailVO.setBottomLimit(product.getBottomLimit());

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detailVO);
    }


    /**
     * 查询产品策略参数信息
     * 按 paramGroup 分组返回：资金配置、仓位配置
     */
    @Override
    public ResultMessage queryProductParam(Integer productId, String strategyId) {
        List<ProductParam> paramList = productParamMapper.selectByProductIdAndStrategyId(productId, strategyId);
        if (paramList == null || paramList.isEmpty()) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, new ArrayList<>());
        }

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
                itemVO.setValue(param.getParamValue());
                itemVO.setUnit(param.getUnit());
                itemVO.setDescribe(param.getDescribe());
                configList.add(itemVO);
            }
            groupVO.setConfig(configList);
            resultList.add(groupVO);
        }

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultList);
    }


    /**
     * 查询产品列表（管理端）
     */
    @Override
    public ResultMessage queryProductListForAdmin(QueryProductSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        Page<OrderProduct> page = (Page<OrderProduct>) orderProductMapper.selectAdminProductList(so.getLevel(), so.getStatus());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 新增产品
     */
    @Override
    @Transactional
    public ResultMessage addProduct(OrderProduct product) {
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        orderProductMapper.insertSelective(product);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "新增成功");
    }

    /**
     * 修改产品
     */
    @Override
    @Transactional
    public ResultMessage updateProduct(OrderProduct product) {
        product.setUpdateTime(new Date());
        orderProductMapper.updateByPrimaryKeySelective(product);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "修改成功");
    }

    /**
     * 删除产品
     */
    @Override
    @Transactional
    public ResultMessage deleteProduct(Integer id) {
        orderProductMapper.deleteByPrimaryKey(id);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
    }

    /**
     * 查询用户交易所API列表
     */
    @Override
    public ResultMessage queryExchangeList(String userId) {
        List<ExchangeListVO> list = apikeyInfoMapper.selectUserExchangeList(userId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", list);
        resultMap.put("total", list.size());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 根据交易所平台获取可用的策略列表
     */
    @Override
    public ResultMessage queryStrategyByExchange(QueryStrategyByExchangeSO so) {
        List<StrategyInfo> list = strategyInfoMapper.selectByFootplate(so.getFootplate());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, list);
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


    /**
     * 获取平台数据汇总
     */
    @Override
    public ResultMessage getPlatformSummary() {
        try {
            // 1. 活跃用户数（拥有运行中或暂停订单的独立用户数）
            int activeUserCount = orderInfoMapper.selectActiveUserCount();

            // 2. 交易总额
            BigDecimal tradeTotalAmount = orderTradeMapper.selectTradeTotalAmount();
            if (tradeTotalAmount == null) {
                tradeTotalAmount = BigDecimal.ZERO;
            }

            // 3. 交易次数
            int tradeTotalCount = orderTradeMapper.selectTradeTotalCount();

            // 4. 年化区间
            BigDecimal minAnnualizedRate = null;
            BigDecimal maxAnnualizedRate = null;
            Map<String, Object> rateRange = orderInfoMapper.selectAnnualizedRateRange();
            if (rateRange != null && !rateRange.isEmpty()) {
                Object minObj = rateRange.get("minRate");
                Object maxObj = rateRange.get("maxRate");
                if (minObj != null) {
                    minAnnualizedRate = new BigDecimal(minObj.toString());
                }
                if (maxObj != null) {
                    maxAnnualizedRate = new BigDecimal(maxObj.toString());
                }
            }

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("activeUserCount", activeUserCount);
            resultMap.put("tradeTotalAmount", tradeTotalAmount);
            resultMap.put("tradeTotalCount", tradeTotalCount);
            resultMap.put("minAnnualizedRate", minAnnualizedRate != null ? minAnnualizedRate : BigDecimal.ZERO);
            resultMap.put("maxAnnualizedRate", maxAnnualizedRate != null ? maxAnnualizedRate : BigDecimal.ZERO);

            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
        } catch (Exception e) {
            log.error("获取平台数据汇总失败", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, ResultMessage.FAILED_MSG);
        }
    }


}
