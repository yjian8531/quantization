package com.example.core.mainbody.so.finance;

import lombok.Data;

/**
 * 页查询推广统计列表SO
 */
@Data
public class QueryCommissionStatisticsSO {

    /** 推广用户账号 **/
    private String account;


    private Integer pageNum;

    private Integer pageSize;

}
