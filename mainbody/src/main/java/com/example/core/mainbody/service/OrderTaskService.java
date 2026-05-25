package com.example.core.mainbody.service;



import com.example.core.common.entity.OrderTask;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.order.OrderTaskSO;

/**
 * 订单任务服务接口
 */
public interface OrderTaskService {

    /**
     * 新增订单任务
     * @param orderTask 订单任务对象
     * @return 操作结果
     */
    ResultMessage addOrderTask(OrderTask orderTask);

    /**
     * 修改订单任务
     * @param orderTask 订单任务对象
     * @return 操作结果
     */
    ResultMessage updateOrderTask(OrderTask orderTask);

    /**
     * 删除订单任务
     * @param orderTaskSO 主键ID
     * @return 操作结果
     */
    ResultMessage deleteOrderTask(OrderTaskSO orderTaskSO);

    /**
     * 查询订单任务详情
     * @param orderTaskSO 主键ID
     * @return 订单任务对象
     */
    ResultMessage getOrderTaskDetail(OrderTaskSO orderTaskSO);

    /**
     * 分页查询订单任务列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 状态（可选）
     * @param tag 标记（可选）
     * @return 订单任务列表
     */
    ResultMessage queryOrderTaskList(Integer pageNum, Integer pageSize, Integer status, Integer tag);

    /**
     * 查询待处理的订单任务列表
     * @return 待处理订单任务列表
     */
    ResultMessage queryPendingOrderTasks();
}
