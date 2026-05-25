package com.example.core.common.mapper;

import com.example.core.common.entity.WalletIncome;
import org.apache.ibatis.annotations.Param;

/**
 * 钱包收入数据访问接口，定义了钱包收入相关的数据库操作方法
 */
public interface WalletIncomeMapper {
    /**
     * 根据主键删除钱包收入记录
     * @param id 钱包收入记录的主键ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入一条钱包收入记录（所有字段都会插入）
     * @param record 钱包收入记录对象
     * @return 插入的记录数
     */
    int insert(WalletIncome record);

    /**
     * 插入一条钱包收入记录（只插入非空字段）
     * @param record 钱包收入记录对象
     * @return 插入的记录数
     */
    int insertSelective(WalletIncome record);

    /**
     * 根据主键查询钱包收入记录
     * @param id 钱包收入记录的主键ID
     * @return 钱包收入记录对象
     */
    WalletIncome selectByPrimaryKey(Integer id);

    /**
     * 根据主键更新钱包收入记录（只更新非空字段）
     * @param record 钱包收入记录对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(WalletIncome record);

    /**
     * 根据主键更新钱包收入记录（更新所有字段）
     * @param record 钱包收入记录对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(WalletIncome record);

    /**
     * 根据交易哈希查询充值记录（用于去重）
     * @param hash 交易哈希值
     * @return 钱包收入记录对象
     */
    WalletIncome selectByHash(@Param("hash") String hash);

}