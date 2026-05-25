package com.example.core.mainbody.service;

import com.example.core.common.entity.MainConfig;
import com.example.core.common.entity.MainInfo;

public interface MainService {

    /**
     * 创建机器人服务器
     * @param mainConfig
     * @return 实例ID
     */
    String create(MainConfig mainConfig);

    /**
     * 续费
     *
     * @param mainInfo
     * @param period
     * @return
     */
    boolean renew(MainInfo mainInfo, int period);

}
