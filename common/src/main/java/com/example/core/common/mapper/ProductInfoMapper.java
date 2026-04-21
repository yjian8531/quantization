package com.example.core.common.mapper;

import com.example.core.common.entity.ProductInfo;
import com.example.core.common.vo.product.ProductListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductInfo record);

    int insertSelective(ProductInfo record);

    ProductInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductInfo record);

    int updateByPrimaryKey(ProductInfo record);

    /** 查询产品列表（支持等级筛选） */
    List<ProductListVO> selectProductList(@Param("level") Integer level);

    /** 查询产品详情 */
    ProductInfo selectProductDetail(@Param("id") Integer id);

    /** 查询产品列表（管理端，所有状态） */
    List<ProductInfo> selectAdminProductList(@Param("level") Integer level, @Param("status") Integer status);
}