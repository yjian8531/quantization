package com.example.core.common.mapper;

import com.example.core.common.entity.DepositAddress;
import org.apache.ibatis.annotations.Param;

public interface DepositAddressMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DepositAddress record);

    int insertSelective(DepositAddress record);

    DepositAddress selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DepositAddress record);

    int updateByPrimaryKey(DepositAddress record);

    /**
     * 根据用户id和网络类型查询充值地址
     * @param userId
     * @param networkType
     * @return
     */
    DepositAddress selectByUserAndNetwork(@Param("userId") String userId, @Param("networkType") String networkType);
    /**
     * 查询未使用的充值地址
     * @param networkType
     * @return
     */
    DepositAddress selectUnusedAddress(@Param("networkType") String networkType);
    /**
     * 绑定地址到用户
     * @param address
     * @param userId
     * @return
     */
    int bindAddressToUser(@Param("address") String address, @Param("userId") String userId);
}