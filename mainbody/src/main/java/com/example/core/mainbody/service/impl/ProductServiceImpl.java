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

//    /**
//     * 查询用户交易所列表
//     */
//    @Override
//    public ResultMessage queryExchangeList(String userId) {
//        List<ExchangeListVO> list = apikeyInfoMapper.selectUserExchangeList(userId);
//        if (list == null) {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "用户未绑定交易所");
//        }
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("list", list);
//        resultMap.put("total", list.size());
//        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
//    }
//
//    /**
//     * 查询交易币对列表
//     */
//    @Override
//    public ResultMessage querySymbolList() {
//        List<SymbolListVO> list = symbolInfoMapper.selectSymbolList();
//        if (list == null) {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "查询失败");
//        }
//        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, list);
//    }
//
//
//    /**
//     * 配置机器人（创建策略订单） 后续根据实际逻辑修改
//     * 采用标准扣费流程：1.冻结余额 -> 2.创建记录 -> 3.实际扣款
//     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public ResultMessage configRobot(String userId, ConfigRobotSO so) {
//        // 1. 查询产品信息，确保产品有效
//        OrderProduct product = orderProductMapper.selectByPrimaryKey(so.getProductId());
//        if (product == null || product.getStatus() != 0) {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "产品不存在或已下架");
//        }
//
//        // 2. 查询用户财务信息
//        UserFinance finance = userFinanceMapper.selectByUserId(userId);
//        BigDecimal fee = product.getMonthlyFee();
//
//        // 校验可用余额是否足够
//        if (finance == null || finance.getValidNum().compareTo(fee) < 0) {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "可用余额不足，请先充值");
//        }
//
//        // ---------------------------------------------------------
//        // 步骤一：冻结余额 (SEAL)
//        // 逻辑：将金额从【可用余额】转移到【冻结金额】，此时总资产不变。
//        // 目的：防止用户在下单过程中把钱转走，确保资金安全。
//        // ---------------------------------------------------------
//        finance.setValidNum(finance.getValidNum().subtract(fee));
//        finance.setFrozenNum(finance.getFrozenNum().add(fee));
//        userFinanceMapper.updateByPrimaryKeySelective(finance);
//
//        // ---------------------------------------------------------
//        // 步骤二：创建账单记录 (状态=0 未完成)
//        // 逻辑：记录一笔支出流水，但标记为“处理中/未完成”。
//        // ---------------------------------------------------------
//        FinanceDetail detail = new FinanceDetail();
//        detail.setUserId(userId);
//        detail.setFinanceNo("FIN" + System.currentTimeMillis());
//        detail.setType(1); // 1=消费
//        detail.setCoinType("USDT");
//        detail.setMoneyNum(fee);
//        detail.setTag("buy"); // 购买服务
//        detail.setDirection(1); // 1=支出
//        detail.setStatus(0); // 0=未完成 (等待实际扣款)
//        detail.setRemarks("购买" + product.getProductName() + "服务");
//        detail.setCreateTime(new Date());
//        financeDetailMapper.insertSelective(detail);
//
//        // ---------------------------------------------------------
//        // 步骤三：创建策略订单记录
//        // 逻辑：生成机器人的运行订单。
//        // ---------------------------------------------------------
//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setOrderNo("ORD" + System.currentTimeMillis() + new Random().nextInt(1000));
//        orderInfo.setOrderName(product.getProductName());
//        orderInfo.setProductId(product.getId());
//        orderInfo.setUserId(userId);
//        orderInfo.setApikeyId(so.getApikeyId());
//        orderInfo.setSymbol(so.getSymbol());
//        orderInfo.setNodeTime(so.getNodeTime());
//        orderInfo.setParamStr(so.getParamStr()); // 优先使用前端传入参数
//        orderInfo.setAnnualizedRate(product.getEstimateRate());
//        orderInfo.setIncome(BigDecimal.ZERO);
//        orderInfo.setIncomeRate(BigDecimal.ZERO);
//        orderInfo.setStatus(0); // 0=启动中
//        orderInfo.setCreateTime(new Date());
//        orderInfo.setUpdateTime(new Date());
//        orderInfoMapper.insertSelective(orderInfo);
//
//        // ---------------------------------------------------------
//        // 步骤四：实际扣款 (MINUS)
//        // 逻辑：上述步骤全部执行成功后，才进行真正的资产扣除。
//        // 动作：冻结金额减少，总资产减少。
//        // ---------------------------------------------------------
//        finance.setFrozenNum(finance.getFrozenNum().subtract(fee)); // 从冻结中扣除
//        finance.setTotalNum(finance.getTotalNum().subtract(fee));   // 总资产实际减少
//        userFinanceMapper.updateByPrimaryKeySelective(finance);
//
//        // ---------------------------------------------------------
//        // 步骤五：更新账单状态为已完成
//        // 逻辑：钱已经扣了，将流水状态改为“已完成”。
//        // ---------------------------------------------------------
//        detail.setStatus(1); // 1=已完成
//        financeDetailMapper.updateByPrimaryKeySelective(detail);
//
//        // ---------------------------------------------------------
//        // 步骤六：更新产品统计数据
//        // ---------------------------------------------------------
//        product.setBuyCount(product.getBuyCount() == null ? 1 : product.getBuyCount() + 1);
//        if (product.getTotalAmount() == null) {
//            product.setTotalAmount(BigDecimal.ZERO);
//        }
//        product.setTotalAmount(product.getTotalAmount().add(fee));
//        orderProductMapper.updateByPrimaryKeySelective(product);
//
//        return new ResultMessage(ResultMessage.SUCCEED_CODE, "配置成功，机器人启动中");
//    }
//    /**
//     * 查询用户机器人列表
//     */
//    @Override
//    public ResultMessage queryRobotList(String userId, QueryRobotSO so) {
//        PageHelper.startPage(so.getPageNum(), so.getPageSize());
//
//        Page<RobotListVO> page = (Page<RobotListVO>) orderInfoMapper.selectUserRobotList(userId, so.getExchange());
//
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("list", page.getResult());
//        resultMap.put("total", page.getTotal());
//
//        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
//    }

}
