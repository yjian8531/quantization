package com.example.core.mainbody.so.financewallet;


import lombok.Data;

/**
 * 更新财务钱包请求参数
 */
@Data
public class UpdateFinanceWalletSO {
    /**
     * 钱包ID
     */
    private Integer id;
    /**
     * 钱包状态(0:正常,1:禁用)
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
}
