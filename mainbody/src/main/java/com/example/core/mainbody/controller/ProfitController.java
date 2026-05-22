package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.ProfitRecord;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.ProfitService;
import com.example.core.mainbody.so.profitrecord.QueryProfitRecordSO;
import com.example.core.mainbody.so.profitrecord.QueryProfitTrendSO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
/**
 * 收益模块Controller
 */
@Slf4j
@RestController
@RequestMapping("/profit")
public class ProfitController extends BaseController {

    @Autowired
    private ProfitService profitService;


    /** 新增收益记录 */
    @PostMapping(value = "/add", produces = {"application/json"})
    public ResultMessage addProfitRecord(@RequestBody ProfitRecord record) {
        try {
            if (record == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "参数不能为空");
            }
            log.info("新增收益记录，参数: {}", record);
            int result = profitService.addProfitRecord(record);
            return result > 0 ? new ResultMessage(ResultMessage.SUCCEED_CODE, "成功")
                    : new ResultMessage(ResultMessage.FAILED_CODE, "失败");
        } catch (Exception e) {
            log.error("新增收益记录异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /** 修改收益记录 */
    @PostMapping(value = "/update", produces = {"application/json"})
    public ResultMessage updateProfitRecord(@RequestBody ProfitRecord record) {
        try {
            if (record == null || record.getId() == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "参数不能为空");
            }
            log.info("修改收益记录，参数: {}", record);
            int result = profitService.updateProfitRecord(record);
            return result > 0 ? new ResultMessage(ResultMessage.SUCCEED_CODE, "成功")
                    : new ResultMessage(ResultMessage.FAILED_CODE, "失败");
        } catch (Exception e) {
            log.error("修改收益记录异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /** 删除收益记录*/
    @PostMapping(value = "/delete", produces = {"application/json"})
    public ResultMessage deleteProfitRecord(@RequestParam Integer id) {
        try {
            if (id == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "ID 不能为空");
            }
            log.info("删除收益记录，ID: {}", id);
            int result = profitService.deleteProfitRecord(id);
            return result > 0 ? new ResultMessage(ResultMessage.SUCCEED_CODE, "成功")
                    : new ResultMessage(ResultMessage.FAILED_CODE, "失败");
        } catch (Exception e) {
            log.error("删除收益记录异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /** 查询收益记录详情 */
    @GetMapping(value = "/detail", produces = {"application/json"})
    public ResultMessage getProfitRecordDetail(@RequestParam Integer id) {
        try {
            if (id == null) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "ID 不能为空");
            }
            ProfitRecord record = profitService.getProfitRecordDetail(id);
            return record != null ? new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, record)
                    : new ResultMessage(ResultMessage.FAILED_CODE, "未找到记录");
        } catch (Exception e) {
            log.error("查询收益记录详情异常", e);
            return new ResultMessage(ResultMessage.FAILED_CODE, "系统异常");
        }
    }

    /**
     * 查询用户收益记录列表 收益记录模块
     */
    @PostMapping(value = "/record/list", produces = {"application/json"})
    public ResultMessage queryProfitRecordList(@RequestBody QueryProfitRecordSO queryProfitRecordSO) {
        UserInfo userInfo = this.getLoginUser();
        return profitService.queryProfitRecordList(userInfo.getUserId(), queryProfitRecordSO);
    }

    /**
     * 查询收益趋势数据（折线图）
     */
    @PostMapping(value = "/trend", produces = {"application/json"})
    public ResultMessage queryProfitTrend(@RequestBody QueryProfitTrendSO queryProfitTrendSO) {
        UserInfo userInfo = this.getLoginUser();
        return profitService.queryProfitTrend(userInfo.getUserId(), queryProfitTrendSO);
    }

    /**
     * 导出收益记录列表（Excel）
     */
    @PostMapping(value = "/record/export", produces = {"application/json"})
    public void exportProfitRecordList(@RequestBody QueryProfitRecordSO queryProfitRecordSO, HttpServletResponse response) throws Exception {
        UserInfo userInfo = this.getLoginUser();
        profitService.exportProfitRecordList(userInfo.getUserId(), queryProfitRecordSO, response);
    }

    /** 查询用户持有的产品列表（下拉框） */
    @GetMapping(value = "/products", produces = {"application/json"})
    public ResultMessage queryUserProducts() {
        UserInfo userInfo = this.getLoginUser();
        return profitService.queryUserProducts(userInfo.getUserId());
    }
}

