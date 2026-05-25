package com.example.core.common.mapper;

import com.example.core.common.entity.OrderProduct;
import com.example.core.common.vo.product.ProductDetailVO;
import com.example.core.common.vo.product.ProductListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单产品映射接口
 * 定义了与订单产品相关的数据库操作方法
 */
public interface OrderProductMapper {
    /**
     * 根据主键删除订单产品记录
     * @param id 订单产品ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条订单产品记录
     * @param record 订单产品对象
     * @return 插入的记录数
     */
    int insert(OrderProduct record);

    /**
     * 选择性插入一条订单产品记录
     * 只插入非空字段
     * @param record 订单产品对象
     * @return 插入的记录数
     */
    int insertSelective(OrderProduct record);

    /**
     * 根据主键查询订单产品记录
     * @param id 订单产品ID
     * @return 订单产品对象
     */
    OrderProduct selectByPrimaryKey(Integer id);

    /**
     * 选择性更新订单产品记录
     * 只更新非空字段
     * @param record 订单产品对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(OrderProduct record);

    /**
     * 根据主键更新订单产品记录
     * 更新所有字段
     * @param record 订单产品对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(OrderProduct record);

    /** 查询产品列表（支持等级筛选） */
    List<ProductListVO> selectProductList(@Param("level") Integer level);

    /** 查询产品详情 */
    ProductDetailVO selectProductDetail(@Param("id") Integer id);

    /** 查询产品列表（管理端，所有状态） */
    List<OrderProduct> selectAdminProductList(@Param("level") Integer level, @Param("status") Integer status);

}