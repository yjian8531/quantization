package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.OrderTask;
import com.example.core.common.mapper.OrderTaskMapper;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderTaskService;
import com.example.core.mainbody.so.order.OrderTaskSO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单任务服务实现类
 */
@Service
@Slf4j
public class OrderTaskServiceImpl implements OrderTaskService {

    @Autowired
    private OrderTaskMapper orderTaskMapper;

    /**
     * 新增订单任务
     * @param orderTask 订单任务对象
     * @return 操作结果
     */
    @Override
    public ResultMessage addOrderTask(OrderTask orderTask) {
        try {
            if (orderTask == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "参数不能为空");
            }
            log.info("新增订单任务，参数: {}", orderTask);
            int result = orderTaskMapper.insertSelective(orderTask);
            return result > 0 ? new ResultMessage(ResultMessage.SUCCEED_CODE, "新增成功") 
                    : new ResultMessage(ResultMessage.FAILED_CODE, "新增失败");
        } catch (Exception e) {
            log.error("新增订单任务异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /**
     * 修改订单任务
     * @param orderTask 订单任务对象
     * @return 操作结果
     */
    @Override
    public ResultMessage updateOrderTask(OrderTask orderTask) {
        try {
            if (orderTask == null || orderTask.getId() == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "参数不能为空");
            }
            log.info("修改订单任务，参数: {}", orderTask);
            int result = orderTaskMapper.updateByPrimaryKeySelective(orderTask);
            return result > 0 ? new ResultMessage(ResultMessage.SUCCEED_CODE, "修改成功") 
                    : new ResultMessage(ResultMessage.FAILED_CODE, "修改失败");
        } catch (Exception e) {
            log.error("修改订单任务异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /**
     * 删除订单任务
     * @param orderTaskSO 主键ID
     * @return 操作结果
     */
    @Override
    public ResultMessage deleteOrderTask(OrderTaskSO orderTaskSO) {
        try {
            if (orderTaskSO.getId() == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "ID不能为空");
            }
            log.info("删除订单任务，ID: {}", orderTaskSO.getId());
            int result = orderTaskMapper.deleteByPrimaryKey(orderTaskSO.getId());
            if (result > 0){
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "删除成功");
            } else {
                return new ResultMessage(ResultMessage.FAILED_CODE, "未找到记录");
            }
        } catch (Exception e) {
            log.error("删除订单任务异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /**
     * 查询订单任务详情
     * @param orderTaskSO 主键ID
     * @return 订单任务对象
     */
    @Override
    public ResultMessage getOrderTaskDetail(OrderTaskSO orderTaskSO) {
        try {
            if (orderTaskSO.getId()== null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "ID不能为空");
            }
            OrderTask orderTask = orderTaskMapper.selectByPrimaryKey(orderTaskSO.getId());
            if(orderTask != null){
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "查询成功", orderTask);
            }else {
                return new ResultMessage(ResultMessage.FAILED_CODE, "未找到记录");
            }
        } catch (Exception e) {
            log.error("查询订单任务详情异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /**
     * 分页查询订单任务列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param status 状态（可选）
     * @param tag 标记（可选）
     * @return 订单任务列表
     */
    @Override
    public ResultMessage queryOrderTaskList(Integer pageNum, Integer pageSize, Integer status, Integer tag) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            
            // 注意：当前Mapper中没有通用的列表查询方法，需要根据实际需求在Mapper中添加
            // 这里暂时返回空列表
            List<OrderTask> list = new java.util.ArrayList<>();
            
            Page<OrderTask> page = (Page<OrderTask>) list;
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("total", page.getTotal());
            resultMap.put("list", page.getResult());
            
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
        } catch (Exception e) {
            log.error("查询订单任务列表异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /**
     * 查询待处理的订单任务列表
     * @return 待处理订单任务列表
     */
    @Override
    public ResultMessage queryPendingOrderTasks() {
        try {
            List<OrderTask> list = orderTaskMapper.selectByPending();
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, list);
        } catch (Exception e) {
            log.error("查询待处理订单任务列表异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }
}
