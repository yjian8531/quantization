package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 充值总量概览
 */
@Data
public class RechargeOverviewVO {
    // 累计充值金额
    private BigDecimal totalAmount;
    // 累计充值次数
    private Integer totalCount;
}
