package com.example.core.common.mapper;

import com.example.core.common.entity.WalletIncome;
import org.apache.ibatis.annotations.Param;

public interface WalletIncomeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(WalletIncome record);

    int insertSelective(WalletIncome record);

    WalletIncome selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WalletIncome record);

    int updateByPrimaryKey(WalletIncome record);

    /**
     * 根据交易哈希查询充值记录（用于去重）
     */
    WalletIncome selectByHash(@Param("hash") String hash);

}