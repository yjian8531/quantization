package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值记录列表项
 */
@Data
public class RechargeRecordVO {
    /** 充值金额 */
    private BigDecimal moneyNum;

    /** 币种 (如 ETH, BSC) */
    private String coinType;

    /** 链类型 (如 ERC-20, BEP20) */
    private String chainType;

    /** 交易哈希 */
    private String txHash;

    /** 充值时间 */
    private Date createTime;
}
