package com.example.core.common.vo.profitrecord;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 获取用户收益记录VO类
 */
@Data
public class ProfitRecordVO {
    
    private String productName;
    
    private String productType;
    
    private BigDecimal profitAmount;
    
    private BigDecimal profitRate;
    
    private Integer status;
    
    private Date createTime;
}
