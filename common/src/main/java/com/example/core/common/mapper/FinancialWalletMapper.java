package com.example.core.common.mapper;

import com.example.core.common.entity.FinancialWallet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FinancialWalletMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinancialWallet record);

    int insertSelective(FinancialWallet record);

    FinancialWallet selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinancialWallet record);

    int updateByPrimaryKey(FinancialWallet record);

    /**
     * 查询所有
     * @return
     */
    List<FinancialWallet> selectAll();
    /**
     * 根据地址查询
     * @param address
     * @return
     */
    FinancialWallet selectByAddress(@Param("address") String address);


    /**
     * 根据用户 ID 和网络类型查询钱包信息（获取充值地址用）
     * @param userId 用户 ID
     * @param type 网络类型（BEP20/TRC20）
     * @return 钱包信息（仅返回正常状态的地址）
     */
    FinancialWallet selectByUserIdAndType(@Param("userId") String userId, @Param("type") String type);


    /**
     * 查询未使用的充值地址（地址池分配）
     * @param type 网络类型
     * @return 未使用的钱包地址
     */
    FinancialWallet selectUnusedAddress(@Param("type") String type);

    /**
     * 绑定地址到用户
     * @param address 钱包地址
     * @param userId 用户ID
     * @return 影响行数
     */
    int bindAddressToUser(@Param("address") String address, @Param("userId") String userId);


    /** 分页查询财务钱包列表 */
    List<FinancialWallet> selectFinanceWalletList(@Param("userId") String userId,
                                                  @Param("type") String type,
                                                  @Param("address") String address,
                                                  @Param("status") Integer status);

}