package com.example.core.mainbody.service;

import com.example.core.common.entity.OrderProduct;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.product.ConfigRobotSO;
import com.example.core.mainbody.so.product.QueryProductSO;
import com.example.core.mainbody.so.product.QueryStrategyByExchangeSO;
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

    /**
     * 查询用户可用交易所列表
     */
    ResultMessage queryExchangeList(String userId);

    /**
     * 查询交易币对列表
     */
    ResultMessage querySymbolList();

    /**
     * 根据交易所平台获取可用的策略列表
     * @param so 查询参数（包含交易所平台筛选）
     * @return 策略列表
     */
    ResultMessage queryStrategyByExchange(QueryStrategyByExchangeSO so);

    /**
     * 查询产品策略参数信息
     * @param productId 产品ID
     * @param strategyId 策略ID
     * @return 分组后的策略参数配置
     */
    ResultMessage queryProductParam(Integer productId, String strategyId);

    /** 获取平台数据汇总 */
    ResultMessage getPlatformSummary();
}
