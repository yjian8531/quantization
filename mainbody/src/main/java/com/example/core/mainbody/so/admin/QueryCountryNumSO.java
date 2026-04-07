package com.example.core.mainbody.so.admin;

import lombok.Data;

/**
 * 查询国家产品数量VO
 */
@Data
public class QueryCountryNumSO {

    /** 产品类型(0:视频养号,1:高级直播,2:直播专线，3:(Socks5) **/
    private Integer type;

}
