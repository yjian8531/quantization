package com.example.core.mainbody.so.product;

import lombok.Data;

/**
 * 产品列表查询 SO
 */
@Data
public class  QueryProductSO {

     /** 页码，默认第 1 页 */
    private Integer pageNum = 1;

    /** 每页条数，默认 10 条 */
    private Integer pageSize = 10;

    /**
     * 产品等级筛选条件
     * null 或 0 = 查询全部等级
     * 1 = 入门级
     * 2 = 标准版
     * 3 = 高级版
     */
    private Integer level;
    /** 状态：0=上架，1=下架，null=全部 */
    private Integer status;
}
