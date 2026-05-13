package com.example.core.mainbody.service;

import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.admin.LoginSO;
import com.example.core.mainbody.so.admin.QueryAdminLogSO;


public interface FinanceWalletService {


    /**
     * 获取用户指定网络的充值地址
     * 业务逻辑：
     * 1. 用户首次请求时，生成该网络专属地址并绑定到用户
     * 2. 后续请求直接返回已绑定的地址
     * 3. 新地址生成后自动同步到监控程序
     *
     * @param userId 用户 ID
     * @param networkType 网络类型（BEP20: 币安链，TRC20: 波场链）
     * @return 充值地址信息
     */
    ResultMessage getDepositAddress(String userId, String networkType);


}
