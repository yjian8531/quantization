package com.example.core.mainbody.service;

import com.example.core.common.entity.ProductInfo;
import com.example.core.common.entity.ProductParam;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.product.QueryProductSO;
/**
 * 产品服务
 * @author lp
 *
 */
public interface ProductService {
    
    /**
     * 查询产品列表
     * @param so 查询参数（包含等级筛选）
     * @return 产品列表
     */
    ResultMessage queryProductList(QueryProductSO so);

    /**
     * 查询产品详情
     * @param productId 产品 ID
     * @return 产品详情（含参数配置）
     */
    ResultMessage queryProductDetail(Integer productId);


    /** 查询产品列表（管理端，所有状态） */
    ResultMessage queryProductListForAdmin(QueryProductSO so);

    /** 新增产品 */
    ResultMessage addProduct(ProductInfo product);

    /** 修改产品 */
    ResultMessage updateProduct(ProductInfo product);

    /** 删除产品 */
    ResultMessage deleteProduct(Integer id);

    /** 查询产品参数列表 */
    ResultMessage queryParamList(Integer productId);

    /** 新增产品参数 */
    ResultMessage addParam(ProductParam param);

    /** 修改产品参数 */
    ResultMessage updateParam(ProductParam param);

    /** 删除产品参数 */
    ResultMessage deleteParam(Integer id);
}
