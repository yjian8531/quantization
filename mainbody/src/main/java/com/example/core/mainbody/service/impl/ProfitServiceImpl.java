package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.ProfitRecord;
import com.example.core.common.mapper.ProfitRecordMapper;
import com.example.core.common.utils.DateUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.vo.profitrecord.ProfitRecordVO;
import com.example.core.common.vo.profitrecord.ProfitTrendVO;
import com.example.core.common.vo.profitrecord.UserProductVO;
import com.example.core.mainbody.service.ProfitService;
import com.example.core.mainbody.so.profitrecord.QueryProfitRecordSO;
import com.example.core.mainbody.so.profitrecord.QueryProfitTrendSO;
import com.example.core.mainbody.utils.ExcelUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
public class ProfitServiceImpl implements ProfitService {

    @Autowired
    private ProfitRecordMapper profitRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addProfitRecord(ProfitRecord record) {
        if (record == null) {
            throw new RuntimeException("记录不能为空");
        }
        record.setCreateTime(new Date());
        if (record.getProfitAmount() != null) {
            record.setStatus(record.getProfitAmount().compareTo(BigDecimal.ZERO) >= 0);
        }
        return profitRecordMapper.insertSelective(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProfitRecord(ProfitRecord record) {
        if (record == null || record.getId() == null) {
            throw new RuntimeException("ID 不能为空");
        }
        return profitRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProfitRecord(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID 不能为空");
        }
        return profitRecordMapper.deleteByPrimaryKey(id);
    }

    @Override
    public ProfitRecord getProfitRecordDetail(Integer id) {
        if (id == null) {
            throw new RuntimeException("ID 不能为空");
        }
        return profitRecordMapper.selectByPrimaryKey(id);
    }



    /**
     * 查询用户收益记录列表
     * 对应原型图下方的策略收益明细列表
     */
    @Override
    public ResultMessage queryProfitRecordList(String userId, QueryProfitRecordSO queryProfitRecordSO) {
        PageHelper.startPage(queryProfitRecordSO.getPageNum(), queryProfitRecordSO.getPageSize());
        Page<ProfitRecordVO> page = (Page<ProfitRecordVO>) profitRecordMapper.selectProfitRecordList(
                userId,
                queryProfitRecordSO.getProductName(),
                queryProfitRecordSO.getProductNo(),
                queryProfitRecordSO.getStartTime(),
                queryProfitRecordSO.getEndTime()
        );
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("list", page.getResult());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 查询收益趋势数据（折线图）
     * 对应原型图顶部的收益趋势图表（年/月/天维度）
     */
    @Override
    public ResultMessage queryProfitTrend(String userId, QueryProfitTrendSO queryProfitTrendSO) {
        // 1. 获取时间维度（year/month/day），默认为年
        String dimension = queryProfitTrendSO.getDimension();
        if (dimension == null || dimension.isEmpty()) {
            dimension = "year";
        }

        // 2. 计算默认时间范围（前端未传时间时）
        String startTime = queryProfitTrendSO.getStartTime();
        String endTime = queryProfitTrendSO.getEndTime();

        if (startTime == null || endTime == null) {
            // 根据维度设置默认查询范围
            Calendar calendar = Calendar.getInstance();
            Date now = new Date();
            endTime = DateUtil.DateToString(now, "yyyy-MM-dd HH:mm:ss");

            switch (dimension) {
                case "year":
                    // 默认查询最近 1 年
                    calendar.add(Calendar.YEAR, -1);
                    break;
                case "month":
                    // 默认查询最近 1 个月
                    calendar.add(Calendar.MONTH, -1);
                    break;
                case "day":
                    // 默认查询最近 7 天
                    calendar.add(Calendar.DAY_OF_MONTH, -7);
                    break;
            }
            startTime = DateUtil.DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
        }

        // 3. 执行查询：按指定维度聚合收益数据
        List<ProfitTrendVO> trendList = profitRecordMapper.selectProfitTrend(
                userId,
                dimension,
                startTime,
                endTime
        );

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, trendList);
    }

    /**
     * 导出收益记录列表（Excel）
     * 对应原型图右上角的蓝色下载按钮
     */
    @Override
    public void exportProfitRecordList(String userId, QueryProfitRecordSO queryProfitRecordSO, HttpServletResponse response) throws Exception {
        // 1. 查询全量数据（导出功能通常不分页，导出所有符合筛选条件的数据）
        List<ProfitRecordVO> list = profitRecordMapper.selectProfitRecordList(
                userId,
                queryProfitRecordSO.getProductName(),
                queryProfitRecordSO.getProductNo(),
                queryProfitRecordSO.getStartTime(),
                queryProfitRecordSO.getEndTime()
        );

        // 2. 组装 Excel 数据格式：第一行为表头，后续为具体内容
        List<List<String>> excelData = new ArrayList<>();

        // 2.1 添加表头
        excelData.add(Arrays.asList("策略名称", "产品类型", "收益金额", "收益率", "盈亏状态", "产生时间"));

        // 2.2 遍历数据并格式化
        for (ProfitRecordVO vo : list) {
            List<String> row = new ArrayList<>();
            row.add(vo.getProductName() != null ? vo.getProductName() : "");
            row.add(vo.getProductType() != null ? vo.getProductType() : "");

            // 收益金额格式化
            row.add(vo.getProfitAmount() != null ? vo.getProfitAmount().toString() : "0.00");
            // 收益率拼接 %
            row.add(vo.getProfitRate() != null ? vo.getProfitRate() + "%" : "0.00%");

            // 状态转换：1-盈利，0-亏损
            row.add(vo.getStatus() != null && vo.getStatus() == 1 ? "盈利" : "亏损");

            // 时间格式化
            row.add(vo.getCreateTime() != null ? DateUtil.DateToString(vo.getCreateTime(), "yyyy-MM-dd HH:mm:ss") : "");

            excelData.add(row);
        }

        // 3. 调用工具类导出 Excel
        // 设置文件名（带时间戳）并处理中文乱码
        String fileName = URLEncoder.encode("收益记录明细_" + System.currentTimeMillis(), "UTF-8") + ".xls";

        ExcelUtil.exportExcel(response, excelData, "收益记录", fileName, 20);
    }

    /**
     * 查询用户产品列表
     * 对应原型图左下角的产品列表
     */
    @Override
    public ResultMessage queryUserProducts(String userId) {
        List<UserProductVO> products = profitRecordMapper.selectUserProducts(userId);
        if (products == null){
            return new ResultMessage(ResultMessage.FAILED_CODE, ResultMessage.FAILED_MSG, null);
        }
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, products);
    }
}
