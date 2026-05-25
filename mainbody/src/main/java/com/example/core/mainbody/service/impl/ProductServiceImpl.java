package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.FinanceDetail;
import com.example.core.common.entity.OrderInfo;
import com.example.core.common.entity.OrderProduct;
import com.example.core.common.entity.UserFinance;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.vo.product.*;
import com.example.core.common.vo.robot.RobotListVO;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.ConfigRobotSO;
import com.example.core.mainbody.so.product.QueryProductSO;
import com.example.core.mainbody.so.robot.QueryRobotSO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
     * 1. 查询产品基础信息（名称、等级、预估年化率、月租、说明文案、累计盈利）
     * 2. 解析 paramConfig（JSON 字符串）为分组表格结构
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
        detailVO.setDescription(product.getDescription());
        detailVO.setCumulativeProfit(product.getCumulativeProfit());

        // 解析 param_config JSON 字符串为表格结构
        List<ParamConfigGroupVO> paramConfigList = parseParamConfig(product.getParamConfig());
        detailVO.setParamConfigList(paramConfigList);

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detailVO);
    }

    /**
     * 解析参数配置 JSON 字符串
     * @param jsonStr 数据库存的 JSON 字符串
     * @return 分组表格结构
     */
    private List<ParamConfigGroupVO> parseParamConfig(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(jsonStr, new TypeReference<List<ParamConfigGroupVO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
