package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
/**
 * 充值接口
 */
@Slf4j
@RestController
@RequestMapping("/deposit")
public class DepositController extends BaseController {

    @Autowired
    private DepositService depositService;

    /**
     * 获取充值地址
     * 前端选择网络后调用，返回二维码对应的地址
     */
    @PostMapping(value = "/address", produces = {"application/json"})
    public ResultMessage getDepositAddress(@RequestParam String networkType) {
        UserInfo userInfo = this.getLoginUser();
        return depositService.getDepositAddress(userInfo.getUserId(), networkType);
    }

    /**
     * 创建充值订单
     * 用户输入金额后调用
     */
    @PostMapping(value = "/order/create", produces = {"application/json"})
    public ResultMessage createDepositOrder(@RequestParam String networkType, @RequestParam BigDecimal amount) {
        UserInfo userInfo = this.getLoginUser();
        return depositService.createDepositOrder(userInfo.getUserId(), networkType, amount);
    }
}
