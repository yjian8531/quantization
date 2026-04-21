package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.ProductInfo;
import com.example.core.common.entity.ProductParam;
import com.example.core.common.mapper.ProductInfoMapper;
import com.example.core.common.mapper.ProductParamMapper;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.vo.product.ProductDetailVO;
import com.example.core.common.vo.product.ProductListVO;
import com.example.core.mainbody.service.ProductService;
import com.example.core.mainbody.so.product.QueryProductSO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoMapper productInfoMapper;
    @Autowired
    private ProductParamMapper productParamMapper;

    /**
     * 查询产品列表
     * 对应原型图：产品精选列表页
     * 功能说明：
     * 1. 根据前端传入的 level 参数筛选产品等级（全部/入门/标准/高级）
     * 2. 只返回 status=1（上架）的产品
     * 3. 返回产品核心展示字段：名称、年化率、月租、已购买人数、累计交易额、盈利占比
     * 4. level 字段直接返回数字（1/2/3），由前端自行转换为标签颜色
     */
    @Override
    public ResultMessage queryProductList(QueryProductSO so) {
        // 1. 开启分页拦截
        PageHelper.startPage(so.getPageNum(), so.getPageSize());

        // 2. 查询产品列表（根据 level 筛选，不传则查全部）
        Page<ProductListVO> page = (Page<ProductListVO>) productInfoMapper.selectProductList(so.getLevel());

        // 3. 组装返回结果（直接返回 VO 列表，不做额外转换）
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 查询产品详情
     * 对应原型图：产品详情弹窗页
     * 功能说明：
     * 1. 查询产品基础信息（名称、等级、年化率、月租、累计盈利、说明文案）
     * 2. 查询基础参数配置（资金配置、初始开仓金额、杠杆倍数、止盈止损比率等）
     * 3. 查询仓位配置（六次加仓的比率和倍数）
     * 4. 参数以键值对列表形式返回，前端直接遍历渲染表格
     */
    @Override
    public ResultMessage queryProductDetail(Integer productId) {
        // 1. 查询产品基础信息
        ProductInfo product = productInfoMapper.selectProductDetail(productId);
        if (product == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "产品不存在或已下架");
        }

        // 2. 组装详情 VO 对象
        ProductDetailVO detailVO = new ProductDetailVO();
        detailVO.setId(product.getId());
        detailVO.setProductName(product.getProductName());
        detailVO.setLevel(product.getLevel());
        detailVO.setAnnualRate(product.getAnnualRate());
        detailVO.setMonthlyFee(product.getMonthlyFee());
        detailVO.setCumulativeProfit(product.getCumulativeProfit());
        detailVO.setDescription(product.getDescription());

        // 3. 查询基础参数（param_group = 'basic'）
        List<Map<String, String>> basicParams = productParamMapper.selectParamsByProductIdAndGroup(productId, "basic");
        detailVO.setBasicParams(basicParams);

        // 4. 查询仓位配置参数（param_group = 'position'）
        List<Map<String, String>> positionParams = productParamMapper.selectParamsByProductIdAndGroup(productId, "position");
        detailVO.setPositionParams(positionParams);

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, detailVO);
    }


    /**
     * 查询产品列表（管理端）
     */
    @Override
    public ResultMessage queryProductListForAdmin(QueryProductSO so) {
        PageHelper.startPage(so.getPageNum(), so.getPageSize());
        // 调用管理端专用 Mapper 方法
        Page<ProductInfo> page = (Page<ProductInfo>) productInfoMapper.selectAdminProductList(so.getLevel(), so.getStatus());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", page.getResult());
        resultMap.put("total", page.getTotal());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 新增产品
     */
    @Override
    @Transactional
    public ResultMessage addProduct(ProductInfo product) {
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        productInfoMapper.insertSelective(product);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "新增成功");
    }

    /**
     * 修改产品
     */
    @Override
    @Transactional
    public ResultMessage updateProduct(ProductInfo product) {
        product.setUpdateTime(new Date());
        productInfoMapper.updateByPrimaryKeySelective(product);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "修改成功");
    }

    /**
     * 删除产品
     */
    @Override
    @Transactional
    public ResultMessage deleteProduct(Integer id) {
        productInfoMapper.deleteByPrimaryKey(id);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
    }

    /**
     * 查询产品参数列表
     */
    @Override
    public ResultMessage queryParamList(Integer productId) {
        List<ProductParam> list = productParamMapper.selectByProductId(productId);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, list);
    }

    /**
     * 新增产品参数
     */
    @Override
    @Transactional
    public ResultMessage addParam(ProductParam param) {
        param.setCreateTime(new Date());
        int insertSelective = productParamMapper.insertSelective(param);
        if (insertSelective <= 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "新增失败");
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "新增成功");
    }

    /**
     * 修改产品参数
     */
    @Override
    @Transactional
    public ResultMessage updateParam(ProductParam param) {
        int primaryKeySelective = productParamMapper.updateByPrimaryKeySelective(param);
        if (primaryKeySelective <= 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "修改失败");
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "修改成功");
    }

    /**
     * 删除产品参数
     */
    @Override
    @Transactional
    public ResultMessage deleteParam(Integer id) {
        int primaryKey = productParamMapper.deleteByPrimaryKey(id);
        if (primaryKey <= 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "删除失败");
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
    }
}
