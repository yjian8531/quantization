package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值记录列表项
 */
@Data
public class RechargeRecordVO {
    // 充值金额
    private BigDecimal moneyNum;
    // 交易类型（ETH/BSC等）
    private String tradeType;
    // 链类型（ERC-20/BEF20等）
    private String chainType;
    // 充值时间
    private Date createTime;
    // 交易哈希（区块链交易ID）
    private String txHash;
}
