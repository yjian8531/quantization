package com.example.core.mainbody.service;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.so.financewallet.*;


public interface FinanceWalletService {


    /**
     * 获取用户指定网络的充值地址
     * 业务逻辑：
     * 1. 用户首次请求时，生成该网络专属地址并绑定到用户
     * 2. 后续请求直接返回已绑定的地址
     * 3. 新地址生成后自动同步到监控程序
     *
     * @param userId 用户 ID
     * @param networkType 网络类型（BEP20: 币安链，TRC20: 波场链）
     * @return 充值地址信息
     */
    ResultMessage getDepositAddress(String userId, String networkType);

    /**
     * 查询财务钱包列表
     * @param so 查询参数
     * @return 钱包列表
     */
    ResultMessage queryFinanceWalletList(QueryFinanceWalletSO so);

    /**
     * 查询财务钱包详情
     * @param so 查询参数
     * @return 钱包详情
     */
    ResultMessage getFinanceWalletDetail(FinanceWalletDetailSO so);

    /**
     * 新增财务钱包
     * @param so 新增参数
     * @return 操作结果
     */
    ResultMessage addFinanceWallet(AddFinanceWalletSO so);

    /**
     * 更新财务钱包信息
     * @param so 更新参数
     * @return 操作结果
     */
    ResultMessage updateFinanceWallet(UpdateFinanceWalletSO so);

    /**
     * 删除财务钱包
     * @param so 删除参数
     * @return 操作结果
     */
    ResultMessage deleteFinanceWallet(DeleteFinanceWalletSO so);


}
