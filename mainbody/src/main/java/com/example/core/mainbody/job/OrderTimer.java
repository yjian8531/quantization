package com.example.core.mainbody.job;


import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.CommonUtil;
import com.example.core.common.utils.DateUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.common.utils.SendQQMailUtil;
import com.example.core.mainbody.service.MainService;
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

import java.math.BigDecimal;
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

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private MainService mainService;

    @Autowired
    private SystemMessageMapper systemMessageMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;


    /**
     * 机器人创建监控
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
                                financeDetailMapper.updateByPrimaryKeySelective(financeDetail);
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
                                //扣除金额
                                userFinanceMapper.updateBalanceByUserId(financeDetail.getUserId(),"minus",financeDetail.getMoneyNum());
                                financeDetail.setStatus(1);//完成状态
                                financeDetail.setUpdateTime(new Date());
                                financeDetailMapper.updateByPrimaryKeySelective(financeDetail);

                                /** 更新产品购买数量 **/
                                orderProductMapper.updateBuyCount(orderInfo.getProductId());
                            }

                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 订单过期自动续费监控
     * 每10分钟扫描一次，距离过期7天内触发自动续费
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void orderExpireMonitor() {
        log.info("订单过期续费监控开始");

        // 查询所有非结束状态的订单
        List<OrderInfo> activeOrders = orderInfoMapper.selectActiveList();
        if (activeOrders == null || activeOrders.isEmpty()) {
            return;
        }

        for (OrderInfo orderInfo : activeOrders) {
            try {
                Date entTime = orderInfo.getEntTime();
                if (entTime == null) {
                    continue;
                }

                // 计算距离过期的天数
                int daysUntilExpire = DateUtil.daysBetween(new Date(), entTime);

                // 距离过期 > 7天，跳过
                if (daysUntilExpire > 7) {
                    continue;
                }

                log.info("订单[{}]将在{}天后过期，触发自动续费", orderInfo.getOrderNo(), daysUntilExpire);

                // 查询主机信息和产品信息
                MainInfo mainInfo = mainInfoMapper.selectByMainNo(orderInfo.getMainNo());
                if (mainInfo == null) {
                    log.warn("订单[{}]的主机信息不存在，跳过续费", orderInfo.getOrderNo());
                    continue;
                }

                OrderProduct product = orderProductMapper.selectByPrimaryKey(orderInfo.getProductId());
                if (product == null) {
                    log.warn("订单[{}]的产品信息不存在，跳过续费", orderInfo.getOrderNo());
                    continue;
                }

                // 计算续费金额：月租金 = 固定月租 + 本金 * 固定百分比
                BigDecimal totalAmount = orderInfo.getTotalAmount() != null
                        ? orderInfo.getTotalAmount() : BigDecimal.ZERO;
                BigDecimal monthlyAmount = product.getMonthlyFee()
                        .add(totalAmount.multiply(product.getMonthlyRatio().multiply(BigDecimal.valueOf(0.01))));

                // 检查用户余额
                UserFinance finance = userFinanceMapper.selectByUserId(orderInfo.getUserId());
                if (finance == null || finance.getValidNum().compareTo(monthlyAmount) < 0) {
                    // 余额不足，发送通知
                    log.warn("订单[{}]续费失败：用户余额不足", orderInfo.getOrderNo());
                    sendRenewalFailureNotice(orderInfo, "余额不足，续费金额: " + monthlyAmount + " USDT");
                    continue;
                }

                // 冻结续费金额
                userFinanceMapper.updateBalanceByUserId(orderInfo.getUserId(), "seal", monthlyAmount);

                boolean renewSuccess = false;
                try {
                    // 调用阿里云续费
                    renewSuccess = mainService.renew(mainInfo, 1);
                } catch (Exception e) {
                    log.error("订单[{}]阿里云续费异常", orderInfo.getOrderNo(), e);
                }

                if (renewSuccess) {
                    // 扣款
                    userFinanceMapper.updateBalanceByUserId(orderInfo.getUserId(), "minus", monthlyAmount);

                    // 更新订单到期时间（延长1个月）
                    orderInfo.setEntTime(DateUtil.addDateMonths(orderInfo.getEntTime(), 1));
                    orderInfo.setUpdateTime(new Date());
                    orderInfoMapper.updateByPrimaryKeySelective(orderInfo);

                    // 创建续费财务记录
                    FinanceDetail financeDetail = new FinanceDetail();
                    financeDetail.setUserId(orderInfo.getUserId());
                    financeDetail.setFinanceNo(CommonUtil.getRandomStr(8));
                    financeDetail.setOrderNo(orderInfo.getOrderNo());
                    financeDetail.setType(1);      // 消费类型
                    financeDetail.setCoinType("USDT");
                    financeDetail.setMoneyNum(monthlyAmount);
                    financeDetail.setPeriod(1);    // 1个月
                    financeDetail.setTag("renew"); // 续费标签
                    financeDetail.setDirection(1); // 支出
                    financeDetail.setWay(0);
                    financeDetail.setStatus(1);    // 完成
                    financeDetail.setCreateTime(new Date());
                    financeDetail.setUpdateTime(new Date());
                    financeDetailMapper.insertSelective(financeDetail);

                    log.info("订单[{}]自动续费成功，续费金额: {}, 新到期时间: {}",
                            orderInfo.getOrderNo(), monthlyAmount,
                            DateUtil.DateToString(orderInfo.getEntTime(), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    // 续费失败，解冻金额
                    userFinanceMapper.updateBalanceByUserId(orderInfo.getUserId(), "unbind", monthlyAmount);

                    // 发送失败通知
                    log.warn("订单[{}]续费失败：阿里云接口返回失败", orderInfo.getOrderNo());
                    sendRenewalFailureNotice(orderInfo, "阿里云续费接口调用失败");
                }

            } catch (Exception e) {
                log.error("订单[{}]自动续费处理异常", orderInfo.getOrderNo(), e);
            }
        }

        log.info("订单过期续费监控结束");
    }

    /**
     * 发送续费失败通知：邮件 + 站内信
     */
    private void sendRenewalFailureNotice(OrderInfo orderInfo, String reason) {
        try {
            // 获取用户信息
            UserInfo userInfo = userInfoMapper.selectByUserId(orderInfo.getUserId());
            String userEmail = userInfo != null ? userInfo.getEmail() : null;

            String title = "【重要】机器人订单续费失败通知";
            String content = "<h3>机器人订单自动续费失败</h3>"
                    + "<p>订单编号：" + orderInfo.getOrderNo() + "</p>"
                    + "<p>订单名称：" + orderInfo.getOrderName() + "</p>"
                    + "<p>到期时间：" + DateUtil.DateToString(orderInfo.getEntTime(), "yyyy-MM-dd HH:mm:ss") + "</p>"
                    + "<p>失败原因：" + reason + "</p>"
                    + "<p>请尽快登录平台手动续费，以免机器人停止运行。</p>";

            // 发送邮件通知
            if (userEmail != null && !userEmail.isEmpty()) {
                try {
                    SendQQMailUtil.send(title, content, userEmail);
                    log.info("续费失败邮件已发送: orderNo={}, email={}", orderInfo.getOrderNo(), userEmail);
                } catch (Exception e) {
                    log.error("续费失败邮件发送异常: orderNo={}", orderInfo.getOrderNo(), e);
                }
            }

            // 插入站内信
            SystemMessage message = new SystemMessage();
            message.setUserId(orderInfo.getUserId());
            message.setTitle(title);
            message.setContent(content);
            message.setType(1);    // 系统通知类型
            message.setIsRead(0);
            message.setCreateTime(new Date());
            systemMessageMapper.insertSelective(message);

            log.info("续费失败站内信已发送: orderNo={}, userId={}", orderInfo.getOrderNo(), orderInfo.getUserId());

        } catch (Exception e) {
            log.error("发送续费失败通知异常: orderNo={}", orderInfo.getOrderNo(), e);
        }
    }

}
