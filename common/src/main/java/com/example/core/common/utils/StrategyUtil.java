package com.example.core.common.utils;
import net.sf.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
/**
 * 策略工具类
 */
public class StrategyUtil {

    private final String ENCRYPT_KEY = "EF01781D65D45A76"; // 必须与 Python 端一致

    private String baseUrl;

    public StrategyUtil(String baseIp){
        this.baseUrl = "http://"+baseIp+":8699";
    }

    /**
     *  查询策略状态
     * @param orderNo 策略订单号
     * @return
     */
    public String getStrategyStatus(String orderNo){
        String url = baseUrl + "/api/strategy/status?orderNo="
                + URLEncoder.encode(orderNo, StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to get strategy status: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询运行策略列表
     * @return
     */
    public String getStrategyList(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/strategy/list"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("HTTP error code: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止策略
     * @param orderNo 策略订单号
     * @return
     */
    public String stopStrategy(String orderNo){
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = "{\"orderNo\":\"" + orderNo + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/strategy/stop"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to stop strategy: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 更新策略平仓
     * @param tag 循环标记(0=正常循序,1=强制平仓,2=保本平仓,3=止赢平仓)
     * @return
     */
    public String updateStrategyTag(Integer tag){
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = "{\"tag\":\"" + tag + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/update/tag"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to stop strategy: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动Python策略
     * @param orderNo 策略订单号
     * @param exchange 交易所
     * @param symbol 交易币对
     * @param apiKey  APIKey
     * @param secret API私钥
     * @param tag 循环标记(0=正常循序,1=强制平仓,2=保本平仓,3=止赢平仓)
     * @param paramStr 策略参数(JSON字符串)
     * @return
     * @throws Exception
     */
    public String startStrategy(String orderNo, String exchange, String symbol, String apiKey, String secret,Integer tag, String paramStr) throws Exception {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderNo", orderNo);
        requestBody.put("exchange", exchange);
        requestBody.put("symbol", symbol);
        requestBody.put("apiKey", apiKey);
        requestBody.put("secret", secret);
        requestBody.put("tag", tag);
        requestBody.put("paramStr", paramStr);

        String jsonBody = JSONObject.fromObject(requestBody).toString();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/strategy/start"))
                .header("Content-Type", "application/json; charset=utf-8")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Failed to start strategy: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
