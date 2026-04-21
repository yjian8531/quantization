package com.example.core.common.vo.finance;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 推广用户信息
 */
@Data
public class PromotionUserVO {
    // 头像
    private String avatar;
    // 昵称
    private String nickName;
    // 手机号
    private String phone;
    // 注册时间
    private Date registerTime;
    // 推广总业绩
    private BigDecimal totalPerformance;
}
