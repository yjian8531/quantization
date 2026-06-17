package com.example.core.mainbody.so.robot;

import lombok.Data;
// 机器人列表查询 SO
@Data
public class QueryRobotSO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    /** 交易所筛选：null=全部，0=币安，1=波场 */
    private Integer exchange;

    /** 排序方式：createTime=创建时间倒序(默认)，income=总收益倒序，runDays=运行时长倒序 */
    private String sortType = "createTime";
}