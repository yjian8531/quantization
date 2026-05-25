package com.example.core.mainbody.so.finance;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户余额更新请求对象
 */
@Data
public class UserBalanceUpdateSO {
    /** 用户ID */
    private String userId;
    /** 操作类型（add：增加,minus：减少,unbind：解冻,seal：冻结） */
    private String tag;
    /** 金额 */
    private BigDecimal amount;
}
