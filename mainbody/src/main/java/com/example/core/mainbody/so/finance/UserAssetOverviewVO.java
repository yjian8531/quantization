package com.example.core.mainbody.so.finance;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 用户资产概览VO
 */
@Data
public class UserAssetOverviewVO {
    
     /** 总资产**/
    private BigDecimal totalAsset;
    
    /** 累计收益**/
    private BigDecimal totalProfit;
    
    /** 是否VIP 0-否 1-是**/
    private Integer isVip;
    
    /** 账号名称**/
    private String accountName;
    
    /** 账号ID**/
    private String accountId;
    
    /** 可用余额**/
    private BigDecimal validNum;
    
    /** 冻结金额**/
    private BigDecimal frozenNum;
}
