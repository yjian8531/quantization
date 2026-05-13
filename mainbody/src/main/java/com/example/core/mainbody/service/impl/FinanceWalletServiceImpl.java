package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.FinancialWallet;
import com.example.core.common.mapper.FinancialWalletMapper;
import com.example.core.common.utils.RedisUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.FinanceWalletService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FinanceWalletServiceImpl implements FinanceWalletService {

    @Autowired
    private FinancialWalletMapper financialWalletMapper;

    /**
     * 获取用户指定网络的充值地址
     * 实现步骤：
     * 1. 校验网络类型合法性（仅支持 BEP20、TRC20）
     * 2. 查询用户是否已绑定该网络地址
     * 3. 已绑定则直接返回，未绑定则从地址池分配并绑定
     * 4. 新地址同步到 Redis 队列，通知监控程序动态加载
     *
     * @param userId 用户 ID
     * @param networkType 网络类型（BEP20/TRC20）
     * @return 地址信息（包含地址和网络类型）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage getDepositAddress(String userId, String networkType) {
        if (!"BEP20".equals(networkType) && !"TRC20".equals(networkType)) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "暂不支持该网络类型");
        }

        FinancialWallet existingWallet = financialWalletMapper.selectByUserIdAndType(userId, networkType);
        if (existingWallet != null) {
            log.info("用户已有{}充值地址：{}", networkType, existingWallet.getAddress());
            return buildAddressResult(existingWallet.getAddress(), networkType);
        }

        FinancialWallet unusedWallet = financialWalletMapper.selectUnusedAddress(networkType);
        if (unusedWallet == null) {
            log.error("{}地址池不足，无法分配", networkType);
            return new ResultMessage(ResultMessage.FAILED_CODE, "当前网络地址不足，请联系客服");
        }

        int bindResult = financialWalletMapper.bindAddressToUser(unusedWallet.getAddress(), userId);
        if (bindResult == 0) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "地址分配失败，请稍后重试");
        }

        // 将新地址加入 Redis 队列，通知监控程序动态添加
        String redisKey = "BEP20".equals(networkType) ? "NEW_ADDRESS_BEP20" : "NEW_ADDRESS_TRC20";
//        addToRedisQueue(redisKey, unusedWallet.getAddress());
        RedisUtil.sadd(redisKey, unusedWallet.getAddress());

        log.info("为用户分配{}充值地址：userId={}, address={}", networkType, userId, unusedWallet.getAddress());
        return buildAddressResult(unusedWallet.getAddress(), networkType);
    }

    /**
     * 将新地址加入 Redis 队列
     * 监控程序会定期扫描该队列，动态添加监控地址
     *
     * @param redisKey Redis 键名
     * @param address 新充值地址
     */
    private void addToRedisQueue(String redisKey, String address) {
        try {
            String cacheData = RedisUtil.get(redisKey);
            JSONArray addressArray = cacheData != null ? JSONArray.fromObject(cacheData) : new JSONArray();
            addressArray.add(address);
            RedisUtil.set(redisKey, addressArray.toString());
            log.info("新地址已加入监控队列：{}", address);
        } catch (Exception e) {
            log.error("地址加入 Redis 队列失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 组装返回结果
     *
     * @param address 充值地址
     * @param networkType 网络类型
     * @return 标准 ResultMessage 响应
     */
    private ResultMessage buildAddressResult(String address, String networkType) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("address", address);
        resultMap.put("networkType", networkType);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, "获取成功", resultMap);
    }
}
