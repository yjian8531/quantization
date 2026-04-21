package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 推广奖励VO
 */
@Data
public class PromotionRewardVO {
    // 推广奖励
    private String title;
    // 标签
    private String tag;
    // 描述
    private String desc;
    // 金额
    private BigDecimal amount;
    // 创建时间
    private Date createTime;
}
