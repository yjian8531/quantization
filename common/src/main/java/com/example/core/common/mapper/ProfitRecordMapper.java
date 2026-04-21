package com.example.core.common.mapper;

import com.example.core.common.entity.ProfitRecord;
import com.example.core.common.vo.profitrecord.ProfitRecordVO;
import com.example.core.common.vo.profitrecord.ProfitTrendVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProfitRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProfitRecord record);

    int insertSelective(ProfitRecord record);

    ProfitRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProfitRecord record);

    int updateByPrimaryKey(ProfitRecord record);

    /**
     * 查询用户收益记录列表（分页）
     * 对应原型图下方的收益明细列表
     */
    List<ProfitRecordVO> selectProfitRecordList(@Param("userId") String userId,
                                                @Param("productName") String productName,
                                                @Param("startTime") String startTime,
                                                @Param("endTime") String endTime);

    /**
     * 查询收益趋势数据（年/月/天维度）
     * 对应原型图顶部折线图
     */
    List<ProfitTrendVO> selectProfitTrend(@Param("userId") String userId,
                                          @Param("dimension") String dimension,
                                          @Param("startTime") String startTime,
                                          @Param("endTime") String endTime);
}