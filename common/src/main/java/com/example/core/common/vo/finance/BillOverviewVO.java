package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillOverviewVO {

    private BigDecimal weekIncome;

    private BigDecimal weekExpense;

    private BigDecimal weekCommission;

    private Integer totalCount;
}
