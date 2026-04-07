package com.example.core.common.utils;


import com.example.core.common.entity.UserDiscount;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class UserDiscountUtil {

    /**
     * 创建规则config
     * @param nodeId 节点ID
     * @param type 类型
     * @param client 客户端
     * @return
     */
    public static JSONObject create(Integer nodeId,Integer type,String client){
        JSONObject json = new JSONObject();
        if(nodeId != null){
            json.put("nodeId",nodeId.toString());
        }

        if(type != null){
            json.put("type",type.toString());
        }


        if(StringUtils.isNotEmpty(client)){
            json.put("client",client);
        }
        return json;
    }

    public static String getConfigShow(Map<Integer,String> nodeMap,String config){
        StringBuilder result = new StringBuilder();
        JSONObject configJson = JSONObject.fromObject(config);
        if(configJson.get("nodeId") != null){
            result.append(nodeMap.get(configJson.getInt("nodeId"))+":");
        }
        if(configJson.get("type") != null){
            if(configJson.getInt("type") == 0){
                result.append("视频养号:");
            }else if(configJson.getInt("type") == 1){
                result.append("直播专线:");
            }else{
                result.append("未知类型:");
            }
        }
        if(configJson.get("client") != null){
            result.append(configJson.getString("client")+":");
        }
        if(result.length() == 0){
            return "全局";
        }else{
            return result.toString().substring(0,result.length()-1);
        }
    }

    /**
     * 计算用户折扣
     * @param nodeId 节点ID
     * @param type 类型
     * @param client 客户端
     * @param list 用户折扣配置列表
     * @return
     */
    public static BigDecimal get(Integer nodeId, Integer type, String client, List<UserDiscount> list){
        JSONObject current = create(nodeId,type,client);

        Integer num = null;
        BigDecimal discount = new BigDecimal("10");

        for(UserDiscount userDiscount : list){
            JSONObject config = JSONObject.fromObject(userDiscount.getConfig());

            boolean bl = true;
            for(Object key : config.keySet()){
                bl = config.getString(key.toString()).equals(current.get(key.toString()));
                if(!bl){
                    break;
                }
            }

            if(bl){
                if(num == null){
                    num = config.size();
                    discount = userDiscount.getDiscount();
                }else if(config.size() > num){
                    num = config.size();
                    discount = userDiscount.getDiscount();
                }
            }
        }
        return discount;
    }

    public static void main(String[] args){
        JSONObject json = create(2,1,null);
        System.out.println(json.toString());
    }

}
