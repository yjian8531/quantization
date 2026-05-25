package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.FinanceWalletService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wallet")
public class WalletController extends BaseController {
    @Autowired
    private FinanceWalletService financeWalletService;

    /**
     * 获取用户充值地址
     * 前端流程：
     * 1. 用户选择链类型（BEP20/TRC20）
     * 2. 调用该接口获取对应充值地址
     * 3. 前端将地址生成二维码展示
     *
     * @param networkType 网络类型（BEP20/TRC20）
     * @return 充值地址信息
     */
    @PostMapping(value = "/address", produces = {"application/json"})
    public ResultMessage getDepositAddress(@RequestParam String networkType) {
        // 从 Token 中获取当前登录用户
        UserInfo currentUser = this.getLoginUser();
        log.info("用户请求获取充值地址：userId={}, networkType={}", currentUser.getUserId(), networkType);
        return financeWalletService.getDepositAddress(currentUser.getUserId(), networkType);
    }

}
