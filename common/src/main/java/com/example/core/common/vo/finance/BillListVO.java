package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillListVO {

    private String financeNo;

    private String title;

    private String tag;

    private String tagName;

    private BigDecimal moneyNum;

    private Integer direction;

    private Date createTime;
}
