package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.OrderProduct;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.QueryProductDetailSO;
import com.example.core.mainbody.so.product.QueryProductParamSO;
import com.example.core.mainbody.so.product.QueryProductSO;
import com.example.core.mainbody.so.product.QueryStrategyByExchangeSO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 产品模块Controller
 * 负责产品相关的展示和管理功能
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    /**
     * 查询产品列表
     * 对应原型图：产品精选列表页
     * 请求示例：POST /product/list {"level": 1}  (1=入门，2=标准，3=高级，null=全部)
     */
    @PostMapping(value = "/list", produces = {"application/json"})
    public ResultMessage queryProductList(@RequestBody QueryProductSO so) {
        return productService.queryProductList(so);
    }

    /**
     * 查询产品详情
     * 对应原型图：产品详情弹窗页
     * 请求示例：POST /product/detail?id=1
     */
    @PostMapping(value = "/detail", produces = {"application/json"})
    public ResultMessage queryProductDetail(@RequestBody QueryProductDetailSO queryProductDetailSO) {
        return productService.queryProductDetail(queryProductDetailSO.getId());
    }

    /**
     * 查询产品列表（管理端）
     * 支持按等级和状态筛选，查看所有产品（包括已下架）
     */
    @PostMapping(value = "/admin/list", produces = {"application/json"})
    public ResultMessage queryProductListForAdmin(@RequestBody QueryProductSO so) {
        return productService.queryProductListForAdmin(so);
    }

    /**
     * 新增产品
     * 管理端使用，创建新的量化产品
     */
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResultMessage addProduct(@RequestBody OrderProduct product) {
        return productService.addProduct(product);
    }

    /**
     * 修改产品
     * 管理端使用，更新产品信息和参数配置
     */
    @PostMapping(value = "/update", produces = {"application/json"})
    public ResultMessage updateProduct(@RequestBody OrderProduct product) {
        return productService.updateProduct(product);
    }

    /**
     * 删除产品
     * 管理端使用，物理删除产品记录
     */
    @PostMapping(value = "/delete", produces = {"application/json"})
    public ResultMessage deleteProduct(@RequestParam Integer id) {
        return productService.deleteProduct(id);
    }

    /**
     * 查询用户交易所API列表
     * 用于配置机器人时选择交易币对
     * 返回系统支持的币对（如ETH/USDT、BTC/USDT等）
     */
    @GetMapping(value = "/exchange/list", produces = {"application/json"})
    public ResultMessage queryExchangeList() {
        UserInfo userInfo = this.getLoginUser();
        return productService.queryExchangeList(userInfo.getUserId());
    }


    /**
     * 查询可用币对列表
     * 用于配置机器人时选择交易币对
     * 返回系统支持的币对（如ETH/USDT、BTC/USDT等）
     */
    @GetMapping(value = "/symbol/list", produces = {"application/json"})
    public ResultMessage querySymbolList() {
        return productService.querySymbolList();
    }

    /**
     * 根据交易所平台查询可用策略列表
     * 用于配置机器人时选择合适的策略
     * 请求示例：POST /product/strategy/list {"footplate": 0}  (null=全部, 0=币安, 1=Gate)
     */
    @PostMapping(value = "/strategy/list", produces = {"application/json"})
    public ResultMessage queryStrategyByExchange(@RequestBody QueryStrategyByExchangeSO so) {
        return productService.queryStrategyByExchange(so);
    }

    /**
     * 查询产品策略参数信息
     * 按分组返回产品策略配置，如：资金配置、仓位配置
     * 请求示例：POST /product/param/config {"productId": 1, "strategyId": "xxx"}
     */
    @PostMapping(value = "/param/config", produces = {"application/json"})
    public ResultMessage queryProductParam(@RequestBody QueryProductParamSO so) {
        return productService.queryProductParam(so.getProductId(), so.getStrategyId());
    }

    /**
     * 获取平台数据汇总
     * 包含：活跃用户数、交易总额、交易次数、年化区间
     */
    @GetMapping(value = "/summary", produces = {"application/json"})
    public ResultMessage getPlatformSummary() {
        return productService.getPlatformSummary();
    }

}
