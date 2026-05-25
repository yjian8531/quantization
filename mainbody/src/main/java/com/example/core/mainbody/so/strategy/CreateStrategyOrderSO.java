package com.example.core.mainbody.so.strategy;

/**
 *  创建策略订单请求参数
 */
public class CreateStrategyOrderSO {

    /**
     * 产品ID
     */
    private Integer productId;

    /**
     * 交易所ApiKeyID
     */
    private Integer apikeyId;

    /**
     * 币对
     */
    private String symbol;

    /**
     * K线时间节点(分钟)
     */
    private String nodeTime;

    /**
     * 策略参数字符串
     */
    private String paramStr;

    /**
     * 策略模板ID（对应StrategyInfo.strategyId）
     */
    private String strategyInfoId;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getApikeyId() {
        return apikeyId;
    }

    public void setApikeyId(Integer apikeyId) {
        this.apikeyId = apikeyId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNodeTime() {
        return nodeTime;
    }

    public void setNodeTime(String nodeTime) {
        this.nodeTime = nodeTime;
    }

    public String getParamStr() {
        return paramStr;
    }

    public void setParamStr(String paramStr) {
        this.paramStr = paramStr;
    }

    public String getStrategyInfoId() {
        return strategyInfoId;
    }

    public void setStrategyInfoId(String strategyInfoId) {
        this.strategyInfoId = strategyInfoId;
    }
}
