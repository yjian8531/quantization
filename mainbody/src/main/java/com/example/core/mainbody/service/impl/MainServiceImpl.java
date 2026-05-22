package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.MainConfig;
import com.example.core.common.entity.MainInfo;
import com.example.core.common.mapper.MainConfigMapper;
import com.example.core.common.mapper.MainInfoMapper;
import com.example.core.common.utils.DateUtil;
import com.example.core.mainbody.service.MainService;
import com.example.core.mainbody.utils.AliyunCaller;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private MainInfoMapper mainInfoMapper;

    @Autowired
    private MainConfigMapper mainConfigMapper;

    /**
     * 创建机器人服务器
     * @param mainConfig
     * @return
     */
    public String create(MainConfig mainConfig) {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("RegionId", mainConfig.getRegion());   // 区域ID
            param.put("ImageId", mainConfig.getSnapshot());   // 镜像ID
            param.put("PlanId", mainConfig.getZone());        // 套餐ID
            param.put("Period", 1);                            // 周期月
            param.put("AutoRenew", false);                     // 是否自动续费
            param.put("Amount", 1);                            // 数量
            param.put("ChargeType", "PrePaid");                // 包月模式
            param.put("ClientToken", UUID.randomUUID().toString());

            JSONObject result = AliyunCaller.exec(mainConfig, "CreateInstances", param);

            if (result.get("InstanceIds") == null || result.getJSONArray("InstanceIds").size() == 0) {
                log.info("阿里云主机创建参数:{}", JSONObject.fromObject(param).toString());
                log.info("阿里云主机创建结果:{}", result.toString());
                return null;
            } else {
                // 主机服务ID
                String instanceId = result.getJSONArray("InstanceIds").getString(0);
                return instanceId;
            }
        } catch (Exception e) {
            log.info("阿里云主机创建异常:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 续费
     * @param mainInfo
     * @param period
     * @return
     */
    public boolean renew(MainInfo mainInfo, int period) {
        String instanceId = mainInfo.getServiceNo();
        MainConfig mainConfig = mainConfigMapper.selectByPrimaryKey(mainInfo.getConfigId());
        // 计算续费周期
        int r = countPeriod(period);

        boolean bl = true;

        while (true) {
            String acction = "RenewInstance"; // 主机续费

            Map<String, Object> param = new HashMap<>();
            param.put("RegionId", mainConfig.getRegion()); // 区域ID
            param.put("InstanceId", instanceId);            // 主机ID
            param.put("Period", r);                          // 周期月

            try {
                log.info("阿里云主机[{}]续费参数:{}", instanceId, JSONObject.fromObject(param).toString());
                JSONObject result = AliyunCaller.exec(mainConfig, acction, param);
                log.info("阿里云主机[{}]续费成功:{}", instanceId, result.toString());
            } catch (Exception e) {
                bl = false;
                log.info("阿里云主机[{}]续费失败:{}", instanceId, e.getMessage());
            }
            // 计算续费后的到期时间
            Date newEndTime = DateUtil.daysBeMonth(mainInfo.getEndTime(), period);
            mainInfo.setEndTime(newEndTime);
            mainInfo.setUpdateTime(new Date());
            mainInfoMapper.updateByPrimaryKeySelective(mainInfo);
            if (period != r) {
                period = period - r;
                if (period < 1) {
                    break;
                }
            } else {
                break;
            }
        }
        return bl;
    }

    /**
     * 计算阿里云续费周期
     * @param period
     * @return
     */
    private int countPeriod(Integer period) {
        int[] items = {1, 3, 6, 12, 24, 36};
        int result = 0;

        boolean bl = false;
        for (int item : items) {
            if (item == period) {
                bl = true;
                break;
            }
        }
        if (bl) {
            result = period;
        } else {
            for (int i = 1; i < items.length; i++) {
                if (period < items[i]) {
                    result = items[i - 1];
                    break;
                }
            }
        }
        return result;
    }

}
