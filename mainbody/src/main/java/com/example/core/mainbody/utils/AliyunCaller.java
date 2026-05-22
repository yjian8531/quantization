package com.example.core.mainbody.utils;

import com.aliyun.teaopenapi.Client;
import com.aliyun.teaopenapi.models.OpenApiRequest;
import com.aliyun.teaopenapi.models.Params;
import com.aliyun.teautil.models.RuntimeOptions;
import com.example.core.common.entity.MainConfig;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AliyunCaller {

    private static Map<String,Client> clientMap = new HashMap<>();

    /**
     * 获取客户端
     * @param mainConfig
     * @return
     * @throws Exception
     */
    public static Client getClient(MainConfig mainConfig) throws Exception {

        if(clientMap.get(mainConfig.getAccount()+":"+mainConfig.getRegion()) == null){
            synchronized(AliyunCaller.class){
                com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                        // 必填，您的 AccessKey ID
                        .setAccessKeyId(mainConfig.getKeyNo())
                        // 必填，您的 AccessKey Secret
                        .setAccessKeySecret(mainConfig.getKeySecret());
                // 访问的域名
                config.endpoint = "swas."+mainConfig.getRegion()+".aliyuncs.com";
                clientMap.put(mainConfig.getAccount()+":"+mainConfig.getRegion(),new Client(config));
            }
            return clientMap.get(mainConfig.getAccount()+":"+mainConfig.getRegion());
        }else{
            return clientMap.get(mainConfig.getAccount()+":"+mainConfig.getRegion());
        }
    }


    private static Params createApiInfo(String acction) throws Exception {
        Params params = new Params()
                // 接口名称
                .setAction(acction)
                // 接口版本
                .setVersion("2020-06-01")
                // 接口协议
                .setProtocol("HTTPS")
                // 接口 HTTP 方法
                .setMethod("POST")
                .setAuthType("AK")
                .setStyle("RPC")
                // 接口 PATH
                .setPathname("/")
                // 接口请求体内容格式
                .setReqBodyType("json")
                // 接口响应体内容格式
                .setBodyType("json");
        return params;
    }

    /**
     * 执行ali API
     * @param regionId 地区编号
     * @param acction API接口
     * @param param 参数
     * @return
     * @throws Exception
     */
    public static JSONObject exec(MainConfig mainConfig, String acction, Map<String,Object> param) throws Exception {

        Client client = getClient(mainConfig);
        Params params = createApiInfo(acction);
        RuntimeOptions runtime = new RuntimeOptions();
        OpenApiRequest request;
        if(param == null || param.keySet().size() == 0){
            request = new OpenApiRequest();
        }else{
            request = new OpenApiRequest().setQuery(com.aliyun.openapiutil.Client.query(param));
        }

        runtime.readTimeout = 30000;

        Map result = client.callApi(params, request, runtime);

        return JSONObject.fromObject(result.get("body"));
    }

}
