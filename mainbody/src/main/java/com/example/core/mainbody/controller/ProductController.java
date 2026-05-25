package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.OrderProduct;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.QueryProductSO;
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
    public ResultMessage queryProductDetail(@RequestParam Integer id) {
        return productService.queryProductDetail(id);
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

}
