package com.example.core.common.mapper;

import com.example.core.common.entity.OrderProduct;
import com.example.core.common.vo.product.ProductDetailVO;
import com.example.core.common.vo.product.ProductListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderProduct record);

    int insertSelective(OrderProduct record);

    OrderProduct selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderProduct record);

    int updateByPrimaryKey(OrderProduct record);

    /** 查询产品列表（支持等级筛选） */
    List<ProductListVO> selectProductList(@Param("level") Integer level);

    /** 查询产品详情 */
    ProductDetailVO selectProductDetail(@Param("id") Integer id);

    /** 查询产品列表（管理端，所有状态） */
    List<OrderProduct> selectAdminProductList(@Param("level") Integer level, @Param("status") Integer status);

}