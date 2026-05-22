package com.example.core.common.vo.product;

import lombok.Data;

/**
 * K线时长列表 VO
 */
@Data
public class KlineListVO {

    /** 时长值（如：15） */
    private String value;

    /** 显示标签（如：15分钟） */
    private String label;

    public KlineListVO(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
