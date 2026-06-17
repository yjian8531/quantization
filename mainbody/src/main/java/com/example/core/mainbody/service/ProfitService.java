package com.example.core.mainbody.service;

import com.example.core.common.entity.OrderPosition;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.profitrecord.QueryProfitRecordSO;
import com.example.core.mainbody.so.profitrecord.QueryProfitTrendSO;

import javax.servlet.http.HttpServletResponse;

public interface ProfitService {

    /** 基础新增（推荐使用 insertSelective） */
    int addProfitRecord(OrderPosition record);

    /** 基础修改（推荐使用 updateByPrimaryKeySelective） */
    int updateProfitRecord(OrderPosition record);

    /** 基础删除 */
    int deleteProfitRecord(Integer id);

    /** 基础查询详情 */
    OrderPosition getProfitRecordDetail(Integer id);
    
    /**
     * 查询用户收益记录列表
     */
    ResultMessage queryProfitRecordList(String userId, QueryProfitRecordSO queryProfitRecordSO);


    /**
     * 查询收益趋势数据（折线图）
     */
    ResultMessage queryProfitTrend(String userId, QueryProfitTrendSO queryProfitTrendSO);


    /**
     * 导出收益记录列表（Excel）
     */
    void exportProfitRecordList(String userId, QueryProfitRecordSO queryProfitRecordSO, HttpServletResponse response) throws Exception;


    ResultMessage queryUserProducts(String userId);

}


