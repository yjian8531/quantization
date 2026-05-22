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

//    /**
//     * 查询用户可用交易所列表
//     */
//    ResultMessage queryExchangeList(String userId);
//
//
//    /** 查询可用币对列表 */
//    ResultMessage querySymbolList();
//
//    /**
//     * 配置机器人（创建策略订单）
//     * @param userId 当前用户 ID
//     * @param so 配置参数
//     */
//    ResultMessage configRobot(String userId, ConfigRobotSO so);
//
//
//    /**
//     * 查询用户机器人列表
//     * @param userId 用户ID
//     * @param so 查询参数
//     */
//    ResultMessage queryRobotList(String userId, com.example.core.mainbody.so.robot.QueryRobotSO so);



}
