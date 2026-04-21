package com.example.core.common.mapper;

import com.example.core.common.entity.ProductParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductParamMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductParam record);

    int insertSelective(ProductParam record);

    ProductParam selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductParam record);

    int updateByPrimaryKey(ProductParam record);


    /** 根据产品 ID 和参数分组查询参数列表 */
    List<Map<String, String>> selectParamsByProductIdAndGroup(
            @Param("productId") Integer productId,
            @Param("paramGroup") String paramGroup
    );

    /** 根据产品 ID 查询所有参数 */
    List<ProductParam> selectByProductId(@Param("productId") Integer productId);
}