package com.example.core.mainbody.test;

import com.example.core.common.utils.AESUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.json.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    private static final String PYTHON_MANAGER_URL = "http://47.237.113.132:8699";
    private static final String ENCRYPT_KEY = "EF01781D65D45A76"; // 必须与 Python 端一致

    public static void  main(String[] args){
        String exchange = "gateio";// binance 或 gateio
        String apiKey = "a7caae932ea3f783ab55c981bc30d41b";
        String secret = "230c7b08e7c17f56c7c8a1be90a9df6ad10bb46d5d5e65867fc5961afe96b9b7";

        Map<String, Object> params = new HashMap<>();
        params.put("symbol","ETH/USDT");

        Map<String,Object> amountObj = new HashMap<>();
        amountObj.put("Lever",10);
        amountObj.put("FirstOrderAmount",45);
        amountObj.put("TakeProfitRatio",0.8);
        amountObj.put("BackRatio",0.05);
        amountObj.put("StopLossRatio",20);
        params.put("amountObj",amountObj);

        List<Map<String,Object>> position = new ArrayList<>();
        position.add(Map.of("ratio",0.5,"multiple",2));
        position.add(Map.of("ratio",1,"multiple",2));
        position.add(Map.of("ratio",1,"multiple",2));
        position.add(Map.of("ratio",5,"multiple",2));
        position.add(Map.of("ratio",20,"multiple",2));
        position.add(Map.of("ratio",30,"multiple",2));
        params.put("position",position);

        //System.out.println(JSONObject.fromObject(params).toString());
        String g = "{\"symbol\":\"ETH/USDT\",\"amountObj\":{\"TotalAmount\":1000,\"BackRatio\":0.05,\"Lever\":10,\"TakeProfitRatio\":0.8,\"StopLossRatio\":20,\"FirstOrderAmount\":25},\"position\":[{\"multiple\":2,\"ratio\":0.5},{\"multiple\":2,\"ratio\":1},{\"multiple\":2,\"ratio\":1},{\"multiple\":2,\"ratio\":5},{\"multiple\":2,\"ratio\":15},{\"multiple\":2,\"ratio\":25}],\"type\":\"spot\"}";

        /*try {
            String str = startStrategy(exchange,apiKey,secret,params);
            System.out.println("输出结果："+str);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        //{"code":"0000","data":{"orderNo":"YJ332nf8sda","pid":708780,"status":"started","strategyId":"STRAT-639395"},"msg":"Strategy started successfully"}
        //String str = getStrategyStatus("STRAT-639395");

        String str = getStrategyList();
        //String str = stopStrategy("STRAT-711383");

        System.out.println(str);

    }

    /**
     * 查询策略状态
     * @param strategyId 8802c532-aaa8-47e0-8773-4f4c8c9da885
     * @return
     */
    public static String getStrategyStatus(String strategyId) {
        // 构建带查询参数的 URL
        String url = PYTHON_MANAGER_URL + "/api/strategy/status?strategyId="
                + URLEncoder.encode(strategyId, StandardCharsets.UTF_8);

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
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询运行策略列表
     * @return
     */
    public static String getStrategyList(){

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PYTHON_MANAGER_URL+"/api/strategy/list"))  // 替换为真实URL
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("HTTP error code: " + response.statusCode());
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 停止策略
     * @param orderNo 8802c532-aaa8-47e0-8773-4f4c8c9da885
     * @return
     */
    public static String stopStrategy(String orderNo){
        HttpClient client = HttpClient.newHttpClient();
        String jsonBody = "{\"orderNo\":\"" + orderNo + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PYTHON_MANAGER_URL + "/api/strategy/stop"))
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
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动Python策略
     * @param exchange 交易所
     * @param apiKey  APIKey
     * @param secret API私钥
     * @param params 策略参数
     * @return
     * @throws Exception
     */
    public static String startStrategy(String exchange, String apiKey, String secret, Map<String, Object> params) throws Exception {
        // 使用对称加密加密 apiKey 和 secret
        String encryptedApiKey = AESUtil.Encrypt(apiKey, ENCRYPT_KEY);
        String encryptedSecret = AESUtil.Encrypt(secret, ENCRYPT_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("exchange", exchange);
        requestBody.put("apiKey", encryptedApiKey);
        requestBody.put("secret", encryptedSecret);
        requestBody.put("paramStr", params);
        requestBody.put("orderNo", "YJ332nf8sda");
        requestBody.put("symbol", "ETH/USDT");

        // 将请求体转为 JSON 字符串
        String jsonBody = new Gson().toJson(requestBody);

        // 创建 JDK 11 HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // 构建 POST 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(PYTHON_MANAGER_URL + "/api/strategy/start"))
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
        } catch (IOException | InterruptedException e) {
            // 恢复中断状态（如果是 InterruptedException）
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException(e);
        }
    }

}
