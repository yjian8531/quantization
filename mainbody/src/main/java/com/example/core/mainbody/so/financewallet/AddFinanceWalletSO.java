package com.example.core.mainbody.so.financewallet;


import lombok.Data;

/**
 * 新增财务钱包请求参数
 */
@Data
public class AddFinanceWalletSO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 钱包类型(ERC20,BEP20,TRC20)
     */
    private String type;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 钱包状态(0:正常,1:禁用)
     */
    private Integer status;
}
