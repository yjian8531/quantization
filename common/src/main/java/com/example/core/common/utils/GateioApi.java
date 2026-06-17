package com.example.core.common.utils;

import io.gate.gateapi.ApiClient;
import io.gate.gateapi.ApiException;
import io.gate.gateapi.api.FuturesApi;
import io.gate.gateapi.api.SpotApi;
import io.gate.gateapi.models.FuturesAccount;
import io.gate.gateapi.models.SpotAccount;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GateioApi {

    private String API_KEY;
    private String SECRET_KEY;
    //private String BASE_PATH = "https://api.gateio.ws/api/v4";
    private String BASE_PATH = "https://api-testnet.gateapi.io/api/v4";

    public GateioApi(String apiKey, String secretKey) {
        this.API_KEY = apiKey;
        this.SECRET_KEY = secretKey;
    }

    public static void main(String[] args){
        GateioApi gateioApi = new GateioApi("39339a162308261855d856e66695705a",
                "fa5730851776fb4918494cfe13178593ba02d4e0b3ad05837eb71a2a7e8c61c6");
        Map<String, BigDecimal> result = gateioApi.getFuturesBalance("usdt");
        System.out.println(JSONObject.fromObject(result).toString());
    }

    /**
     * 获取gate期货合约余额
     * @param settle 结算货币，如 "usdt"、"btc"
     * @return 余额信息
     */
    public Map<String, BigDecimal> getFuturesBalance(String settle) {
        Map<String, BigDecimal> result = null;
        try {
            ApiClient client = new ApiClient(API_KEY,SECRET_KEY);
            client.setBasePath(BASE_PATH);

            FuturesApi futuresApi = new FuturesApi(client);
            FuturesAccount account = futuresApi.listFuturesAccounts(settle);

            result = new HashMap<>();
            if (account != null && account.getAvailable() != null) {
                BigDecimal available = new BigDecimal(account.getAvailable());
                if (available.compareTo(BigDecimal.ZERO) > 0) {
                    result.put(settle.toUpperCase(), available);
                }
            }
        } catch (ApiException e) {
            log.error("获取Gate期货合约余额失败, settle={}, error={}", settle, e.getMessage(), e);
        }
        return result;
    }

    /**
     * 获取gate现货余额
     * @return 各币种余额映射
     *//*
    public Map<String, BigDecimal> getSpotBalance() {
        Map<String, BigDecimal> result = new HashMap<>();
        try {
            ApiClient client = new ApiClient(API_KEY,SECRET_KEY);
            client.setBasePath(BASE_PATH);

            SpotApi spotApi = new SpotApi(client);
            List<SpotAccount> accounts = spotApi.listSpotAccounts();

            if (accounts != null) {
                for (SpotAccount account : accounts) {
                    if (account.getAvailable() != null && account.getCurrency() != null) {
                        BigDecimal available = new BigDecimal(account.getAvailable());
                        if (available.compareTo(BigDecimal.ZERO) > 0) {
                            result.put(account.getCurrency().toUpperCase(), available);
                        }
                    }
                }
            }
        } catch (ApiException e) {
            log.error("获取Gate现货余额失败, error={}", e.getMessage(), e);
        }
        return result;
    }*/

}
