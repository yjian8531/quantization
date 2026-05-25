package com.example.core.mainbody.so.product;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 配置机器人 SO
 */
@Data
public class ConfigRobotSO {

     /** 产品 ID */
    private Integer productId;

    /** 交易所 API Key ID */
    private Integer apikeyId;

    /** 币对 (如 ETH/USDT) */
    private String symbol;

    /** K 线时长 (如 15) */
    private String nodeTime;

    /** 策略参数 */
    private String paramStr;
}
