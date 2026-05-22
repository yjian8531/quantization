package com.example.core.common.vo.product;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 币对列表 VO
 */
@Data
public class SymbolListVO {

    /** 主键id */
    private Integer id;

    /** 币对 */
    private String symbol;

    /** 真实值 */
    private BigDecimal realVal;
}
