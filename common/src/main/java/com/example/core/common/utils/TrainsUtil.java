package com.example.core.common.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TrainsUtil {

    public static synchronized String ecex(Map<String, Object> params)throws Exception {
        String url = (String)params.get("url");
        String type  = (String)params.get("type");
        String appid  = (String)params.get("appid");
        Date start = new Date();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = null;
        if("GET".equals(type.toUpperCase())){
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("X-MBX-APIKEY", appid)
                    .header("client_SDK_Version", "binance_futures-1.0.1-java")
                    .build();
        }else if("POST".equals(type.toUpperCase())){
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("X-MBX-APIKEY", appid)
                    .header("client_SDK_Version", "binance_futures-1.0.1-java")
                    .build();

        }else if("DELETE".equals(type.toUpperCase())){
            request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("X-MBX-APIKEY", appid)
                    .header("client_SDK_Version", "binance_futures-1.0.1-java")
                    .build();

        }
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Date end = new Date();
        if((end.getTime() - start.getTime()) < 50){
            Thread.sleep(50);
        }

        return response.body();
    }



    public static String mapToStringURLEncoder(Map<String, Object> map)throws Exception {
        StringBuffer sb = new StringBuffer();
        SortedMap<String, Object> params = new TreeMap<>();
        for (String key : map.keySet()) {
            params.put(key,map.get(key));

        }
        for(String key : params.keySet()){
            String str = key + "=" + URLEncoder.encode(params.get(key).toString(), "UTF-8" ) + "&";
            sb.append(str);
        }
        String result = sb.toString().substring(0, sb.length() - 1);
        return result;
    }
}
