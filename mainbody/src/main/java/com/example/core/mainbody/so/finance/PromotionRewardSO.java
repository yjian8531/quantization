package com.example.core.mainbody.so.finance;

import lombok.Data;

@Data
public class PromotionRewardSO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    /**
     * 类型(0:购买返佣,1:续费返佣,2邀请激活,3托管达标,4量化分润)
     */
     private Integer type;
}
