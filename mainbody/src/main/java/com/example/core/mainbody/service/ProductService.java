package com.example.core.mainbody.service;

import com.example.core.common.entity.OrderProduct;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.product.ConfigRobotSO;
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
    ResultMessage addProduct(OrderProduct product);

    /** 修改产品 */
    ResultMessage updateProduct(OrderProduct product);

    /** 删除产品 */
    ResultMessage deleteProduct(Integer id);

    ResultMessage queryExchangeList(String userId);

    ResultMessage querySymbolList();
}
