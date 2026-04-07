package com.example.core.mainbody.so.user;

import lombok.Data;

/**
 * 修改站内信息已读状态SO
 */
@Data
public class UpdateMessageStatusSO {

    private Integer id;

    /** 状态(0:未读,1:已读) **/
    private Integer status;

}
