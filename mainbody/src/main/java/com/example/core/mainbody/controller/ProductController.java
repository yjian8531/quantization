package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.ProductInfo;
import com.example.core.common.entity.ProductParam;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.QueryProductSO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * 产品模块
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
     * 对应原型图：产品详情页
     * 请求示例：POST /product/detail?id=1
     */
    @PostMapping(value = "/detail", produces = {"application/json"})
    public ResultMessage queryProductDetail(@RequestParam Integer id) {
        return productService.queryProductDetail(id);
    }

    /** 查询产品列表（管理端） */
    @PostMapping(value = "/admin/list", produces = {"application/json"})
    public ResultMessage queryProductListForAdmin(@RequestBody QueryProductSO so) {
        return productService.queryProductListForAdmin(so);
    }

    /** 新增产品 */
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResultMessage addProduct(@RequestBody ProductInfo product) {
        return productService.addProduct(product);
    }

    /** 修改产品 */
    @PostMapping(value = "/update", produces = {"application/json"})
    public ResultMessage updateProduct(@RequestBody ProductInfo product) {
        return productService.updateProduct(product);
    }

    /** 删除产品 */
    @PostMapping(value = "/delete", produces = {"application/json"})
    public ResultMessage deleteProduct(@RequestParam Integer id) {
        return productService.deleteProduct(id);
    }

    /** 查询产品参数列表 */
    @PostMapping(value = "/param/list", produces = {"application/json"})
    public ResultMessage queryParamList(@RequestParam Integer productId) {
        return productService.queryParamList(productId);
    }

    /** 新增产品参数 */
    @PostMapping(value = "/param/add", produces = {"application/json"})
    public ResultMessage addParam(@RequestBody ProductParam param) {
        return productService.addParam(param);
    }

    /** 修改产品参数 */
    @PostMapping(value = "/param/update", produces = {"application/json"})
    public ResultMessage updateParam(@RequestBody ProductParam param) {
        return productService.updateParam(param);
    }

    /** 删除产品参数 */
    @PostMapping(value = "/param/delete", produces = {"application/json"})
    public ResultMessage deleteParam(@RequestParam Integer id) {
        return productService.deleteParam(id);
    }
}
