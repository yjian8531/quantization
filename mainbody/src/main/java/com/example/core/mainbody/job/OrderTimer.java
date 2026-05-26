package com.example.core.mainbody.job;


import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.CommonUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.OrderService;
import com.example.core.mainbody.utils.AliyunCaller;
import com.example.core.mainbody.utils.PythonExecutor;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class OrderTimer {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private MainInfoMapper mainInfoMapper;

    @Autowired
    private OrderTaskMapper orderTaskMapper;

    @Autowired
    private MainConfigMapper mainConfigMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FinanceDetailMapper financeDetailMapper;

    @Autowired
    private UserFinanceMapper userFinanceMapper;

    @Autowired
    private StrategyInfoMapper strategyInfoMapper;


    /**
     * 实例待续费监控
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void orderInit() {
        List<OrderTask> taskList = orderTaskMapper.selectByPending();
        for(OrderTask orderTask : taskList){
            try{

                OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderTask.getOrderNo());
                MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());
                MainConfig mainConfig = mainConfigMapper.selectByPrimaryKey(mainInfo.getConfigId());
                String instanceId = mainInfo.getServiceNo();

                if(orderTask.getTag() == 0){ //创建服务器监控


                    /** 查询实例信息 **/
                    Map<String,Object> param = new HashMap<>();
                    param.put("InstanceIds","[\""+instanceId+"\"]");

                    JSONObject result = AliyunCaller.exec(mainConfig, "ListInstances", param);
                    JSONArray instanceList = result.getJSONArray("Instances");
                    if(instanceList.size() > 0) {
                        JSONObject instance = instanceList.getJSONObject(0);
                        String status = instance.getString("Status");
                        if ("Running".toLowerCase().equals(status.toLowerCase())) {//正常运行状态

                            mainInfo.setConnectIp(instance.getString("PublicIpAddress"));
                            mainInfo.setConnectPort(22);
                            mainInfo.setConnectAccount("root");
                            int j = CommonUtil.getRandom(12, 18);
                            String pwd = CommonUtil.getPsw(j);
                            mainInfo.setConnectPwd(pwd);
                            mainInfo.setUpdateTime(new Date());
                            mainInfoMapper.updateByPrimaryKeySelective(mainInfo);


                            /** 配置安全组加速端口 **/
                            Map<String,Object> p = new HashMap<>();
                            p.put("InstanceId",mainInfo.getServiceNo());
                            p.put("RuleProtocol","TCP+UDP");
                            p.put("Port",8699);
                            AliyunCaller.exec(mainConfig,"CreateFirewallRule",p);


                            /** 配置ROOT 密码 **/
                            Map<String,Object> m = new HashMap<>();
                            m.put("InstanceId",mainInfo.getServiceNo());
                            m.put("Password",mainInfo.getConnectPwd());
                            m.put("ClientToken", UUID.randomUUID().toString());
                            AliyunCaller.exec(mainConfig,"UpdateInstanceAttribute",m);

                            /** 重启 **/
                            Map<String,Object> a = new HashMap<>();
                            a.put("InstanceId",mainInfo.getServiceNo());
                            a.put("ClientToken",UUID.randomUUID().toString());
                            AliyunCaller.exec(mainConfig,"RebootInstance",a);


                            orderTask.setTag(1);//
                            orderTask.setUpdateTime(new Date());
                            orderTaskMapper.updateByPrimaryKeySelective(orderTask);

                        }else if(status.indexOf("Disabled") > -1) {//主机创建失败

                            FinanceDetail financeDetail = financeDetailMapper.selectBuyByOrderNo(orderInfo.getOrderNo());
                            if(financeDetail.getStatus() == 0){
                                //解冻金额
                                userFinanceMapper.updateBalanceByUserId(financeDetail.getUserId(),"seal",financeDetail.getMoneyNum());
                                financeDetail.setStatus(2);//取消状态
                                financeDetail.setUpdateTime(new Date());
                                financeDetailMapper.insertSelective(financeDetail);
                            }


                            orderTask.setRemark("机器人服务器创建状态:"+status);
                            orderTask.setStatus(2);//机器人服务器创建失败
                            orderTask.setUpdateTime(new Date());
                            orderTaskMapper.updateByPrimaryKeySelective(orderTask);

                        }
                    }
                }else if(orderTask.getTag() == 1){ //启动机器人策略

                    /** 查询实例信息 **/
                    Map<String,Object> param = new HashMap<>();
                    param.put("InstanceIds","[\""+instanceId+"\"]");

                    JSONObject result = AliyunCaller.exec(mainConfig, "ListInstances", param);
                    JSONArray instanceList = result.getJSONArray("Instances");
                    if(instanceList.size() > 0) {
                        JSONObject instance = instanceList.getJSONObject(0);
                        String status = instance.getString("Status");
                        if ("Running".toLowerCase().equals(status.toLowerCase())) {//正常运行状态
                            /** 上传策略文件 **/
                            StrategyInfo strategyInfo = strategyInfoMapper.selectByStrategyId(orderInfo.getStrategyId());
                            Boolean bl = PythonExecutor.exec(mainInfo.getConnectIp(),22,"root",mainInfo.getConnectPwd(),strategyInfo.getContent());

                            if(bl){
                                ResultMessage resultMessage = orderService.startStrategyOrder(orderInfo.getOrderNo());
                                if(resultMessage.getCode().equals(ResultMessage.SUCCEED_CODE)){
                                    orderTask.setStatus(1);//机器人启动成功

                                }else{
                                    orderTask.setStatus(2);//机器人启动失败
                                    orderTask.setRemark("机器人启动失败:"+resultMessage.getMsg());
                                }
                                orderTask.setUpdateTime(new Date());
                                orderTaskMapper.updateByPrimaryKeySelective(orderTask);


                                /** 扣除金额 **/
                                FinanceDetail financeDetail = financeDetailMapper.selectBuyByOrderNo(orderInfo.getOrderNo());
                                if(financeDetail.getStatus() == 0){
                                    //解冻金额
                                    userFinanceMapper.updateBalanceByUserId(financeDetail.getUserId(),"unbind",financeDetail.getMoneyNum());
                                    financeDetail.setStatus(1);//完成状态
                                    financeDetail.setUpdateTime(new Date());
                                    financeDetailMapper.insertSelective(financeDetail);
                                }
                            }else{
                                log.info("订单[{}]上传策略文件失败。。。。。。。。。",orderInfo.getOrderNo());
                            }

                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
