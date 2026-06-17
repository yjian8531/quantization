package com.example.core.mainbody.so;

import lombok.Data;

/**
 * 添加用户交易所API请求参数
 */
@Data
public class AddApikeySO {

    /**
     * 交易所平台(0:币安,1:gate)
     */
    private Integer footplate;

    /**
     * 名称
     */
    private String name;

    /**
     * APP_key
     */
    private String apikey;

    /**
     * APP_secret
     */
    private String secret;

}
