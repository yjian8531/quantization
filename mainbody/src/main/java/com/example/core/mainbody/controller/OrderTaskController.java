package com.example.core.mainbody.controller;


import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.OrderTask;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderTaskService;
import com.example.core.mainbody.so.order.OrderTaskSO;
import com.example.core.mainbody.so.order.QueryOrderTaskListSO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单任务模块Controller
 * 负责订单任务的CRUD功能
 */
@Slf4j
@RestController
@RequestMapping("/order/task")
public class OrderTaskController extends BaseController {

    @Autowired
    private OrderTaskService orderTaskService;

    /**
     * 新增订单任务
     * @param orderTask 订单任务对象
     * @return 操作结果
     */
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResultMessage addOrderTask(@RequestBody OrderTask orderTask) {
        log.info("新增订单任务请求");
        return orderTaskService.addOrderTask(orderTask);
    }

    /**
     * 修改订单任务
     * @param orderTask 订单任务对象
     * @return 操作结果
     */
    @PostMapping(value = "/update", produces = {"application/json"})
    public ResultMessage updateOrderTask(@RequestBody OrderTask orderTask) {
        log.info("修改订单任务请求");
        return orderTaskService.updateOrderTask(orderTask);
    }

    /**
     * 删除订单任务
     * @param orderTaskSO 主键ID
     * @return 操作结果
     */
    @PostMapping(value = "/delete", produces = {"application/json"})
    public ResultMessage deleteOrderTask(@RequestBody OrderTaskSO orderTaskSO) {
        log.info("删除订单任务请求，ID: {}", orderTaskSO);
        return orderTaskService.deleteOrderTask(orderTaskSO);
    }

    /**
     * 查询订单任务详情
     * @param orderTaskSO 主键ID
     * @return 订单任务对象
     */
    @GetMapping(value = "/detail", produces = {"application/json"})
    public ResultMessage getOrderTaskDetail(@RequestBody OrderTaskSO orderTaskSO) {
        log.info("查询订单任务详情，ID: {}", orderTaskSO);
        return orderTaskService.getOrderTaskDetail(orderTaskSO);
    }

    /**
     * 分页查询订单任务列表
     * @param so 查询请求对象
     * @return 订单任务列表
     */
    @PostMapping(value = "/list", produces = {"application/json"})
    public ResultMessage queryOrderTaskList(@RequestBody QueryOrderTaskListSO so) {
        log.info("查询订单任务列表");
        return orderTaskService.queryOrderTaskList(so.getPageNum(), so.getPageSize(), so.getStatus(), so.getTag());
    }


    /**
     * 查询待处理的订单任务列表
     * @return 待处理订单任务列表
     */
    @GetMapping(value = "/pending/list", produces = {"application/json"})
    public ResultMessage queryPendingOrderTasks() {
        log.info("查询待处理订单任务列表");
        return orderTaskService.queryPendingOrderTasks();
    }
}
