package com.example.core.common.utils;


import com.binance.client.constant.BinanceApiConstants;
import com.binance.client.exception.BinanceApiException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BinanceApi {

    private String API_KEY;
    private String SECRET_KEY;
    private String GET = "GET";

    public BinanceApi(String apiKey, String secretKey){
        this.API_KEY = apiKey;
        this.SECRET_KEY = secretKey;
    }

    /**
     * 获取现货余额
     * @return
     */
    public Map<String,BigDecimal> getBalance(){
        String url = "https://api.binance.com/api/v3/account";
        Map<String,Object> param = new HashMap();
        //签名
        String p = createSignature(API_KEY,SECRET_KEY,param);

        url = url+"?"+p;

        String str = "";
        Map<String, Object> params = new HashMap<>();
        params.put("url",url);
        params.put("type",GET);
        params.put("appid",API_KEY);
        try{
            str = TrainsUtil.ecex(params);
            log.info("查询余额返回:{}",str);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(StringUtils.isNotEmpty(str)){
            JSONObject accountJson = JSONObject.fromObject(str);
            JSONArray balanceArray = accountJson.getJSONArray("balances");
            Map<String,BigDecimal> result = new HashMap<>();

            for(int i=0 ; i <balanceArray.size() ; i++){
                JSONObject balance = balanceArray.getJSONObject(i);
                BigDecimal balanceNum = new BigDecimal(balance.getString("free"));
                if(balanceNum.compareTo(new BigDecimal("0")) > 0){
                    result.put(balance.getString("asset").toUpperCase(),balanceNum);
                }
            }

            return result;
        }else{
            return null;
        }


    }

    /**
     * 获取合约余额
     * @return
     */
    public Map<String,BigDecimal> getBalanceContract(){
        String url = "https://fapi.binance.com/fapi/v3/balance";
        Map<String,Object> param = new HashMap();
        //签名
        String p = createSignature(API_KEY,SECRET_KEY,param);

        url = url+"?"+p;

        String str = "";
        Map<String, Object> params = new HashMap<>();
        params.put("url",url);
        params.put("type",GET);
        params.put("appid",API_KEY);
        try{
            str = TrainsUtil.ecex(params);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(StringUtils.isNotEmpty(str)){

            JSONArray balanceArray = JSONArray.fromObject(str);
            Map<String,BigDecimal> result = new HashMap<>();

            for(int i=0 ; i <balanceArray.size() ; i++){
                JSONObject balance = balanceArray.getJSONObject(i);
                BigDecimal balanceNum = new BigDecimal(balance.getString("availableBalance"));//可用金额
                if(balanceNum.compareTo(new BigDecimal("0")) > 0){
                    result.put(balance.getString("asset").toUpperCase(),balanceNum);
                }
            }

            return result;
        }else{
            return null;
        }
    }

    /**
     * 获取合约余额明细
     * @return
     */
    public Map<String,Map<String,BigDecimal>> getBalanceContractDetail(){
        String url = "https://fapi.binance.com/fapi/v3/balance";
        Map<String,Object> param = new HashMap();
        //签名
        String p = createSignature(API_KEY,SECRET_KEY,param);

        url = url+"?"+p;

        String str = "";
        Map<String, Object> params = new HashMap<>();
        params.put("url",url);
        params.put("type",GET);
        params.put("appid",API_KEY);
        try{
            str = TrainsUtil.ecex(params);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(StringUtils.isNotEmpty(str)){

            JSONArray balanceArray = JSONArray.fromObject(str);
            Map<String,Map<String,BigDecimal>> result = new HashMap<>();

            for(int i=0 ; i <balanceArray.size() ; i++){
                JSONObject balance = balanceArray.getJSONObject(i);

                BigDecimal balanceNum = new BigDecimal(balance.getString("availableBalance"));
                BigDecimal lockedNum = new BigDecimal(balance.getString("crossUnPnl"));
                if(balanceNum.compareTo(new BigDecimal("0")) > 0 || lockedNum.compareTo(new BigDecimal("0")) > 0){
                    Map<String,BigDecimal> data = new HashMap<>();
                    data.put("balance",balanceNum);
                    data.put("locked",lockedNum);
                    result.put(balance.getString("asset").toUpperCase(),data);
                }
            }

            return result;
        }else{
            return null;
        }
    }


    /**
     * 获取现货余额明细
     * @return
     */
    public Map<String,Map<String,BigDecimal>> getBalanceDetail(){
        String url = "https://api.binance.com/api/v3/account";
        Map<String,Object> param = new HashMap();
        //签名
        String p = createSignature(API_KEY,SECRET_KEY,param);

        url = url+"?"+p;

        String str = "";
        Map<String, Object> params = new HashMap<>();
        params.put("url",url);
        params.put("type",GET);
        params.put("appid",API_KEY);
        try{
            str = TrainsUtil.ecex(params);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(StringUtils.isNotEmpty(str)){
            JSONObject accountJson = JSONObject.fromObject(str);
            JSONArray balanceArray = accountJson.getJSONArray("balances");
            Map<String,Map<String,BigDecimal>> result = new HashMap<>();

            for(int i=0 ; i <balanceArray.size() ; i++){
                JSONObject balance = balanceArray.getJSONObject(i);
                BigDecimal balanceNum = new BigDecimal(balance.getString("free"));
                BigDecimal lockedNum = new BigDecimal(balance.getString("locked"));
                if(balanceNum.compareTo(new BigDecimal("0")) > 0 || lockedNum.compareTo(new BigDecimal("0")) > 0){
                    Map<String,BigDecimal> data = new HashMap<>();
                    data.put("balance",balanceNum);
                    data.put("locked",lockedNum);
                    result.put(balance.getString("asset").toUpperCase(),data);
                }
            }

            return result;
        }else{
            return null;
        }


    }



    /**
     ** 签名
     * @param accessKey
     * @param secretKey
     * @param param
     * @return
     */
    public static String createSignature(String accessKey, String secretKey, Map<String, Object> param) {

        param.put("recvWindow", Long.toString(BinanceApiConstants.DEFAULT_RECEIVING_WINDOW));
        param.put("timestamp", Long.toString(System.currentTimeMillis()));
        Mac hmacSha256;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
                    "[Signature] No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new BinanceApiException(BinanceApiException.RUNTIME_ERROR,
                    "[Signature] Invalid key: " + e.getMessage());
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            if (!("").equals(stringBuilder.toString())) {
                stringBuilder.append("&");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(CommonUtil.urlEncode(entry.getValue().toString()));
        }
        String payload =  stringBuilder.toString();
        String actualSign = new String(Hex.encodeHex(hmacSha256.doFinal(payload.getBytes())));

        return payload+"&signature="+actualSign;

    }


}
