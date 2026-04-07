package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.AdminInfo;
import com.example.core.common.entity.AdminLog;
import com.example.core.common.entity.PowerBinding;
import com.example.core.common.entity.PowerShow;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.CommonUtil;
import com.example.core.common.utils.MD5;
import com.example.core.common.utils.RedisUtil;
import com.example.core.common.utils.ResultMessage;
import com.example.core.mainbody.service.AdminService;
import com.example.core.mainbody.so.admin.LoginSO;
import com.example.core.mainbody.so.admin.QueryAdminLogSO;
import com.example.core.mainbody.utils.InterceptorUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminInfoMapper adminInfoMapper;

    @Autowired
    private AdminLogMapper adminLogMapper;

    @Autowired
    private PowerBindingMapper powerBindingMapper;

    @Autowired
    private PowerShowMapper powerShowMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

//    @Autowired
//    private ProductInfoMapper productInfoMapper;
//
//    @Autowired
//    private FinanceDetailMapper financeDetailMapper;
//
//    @Autowired
//    private TimerTaskMapper timerTaskMapper;

    /**
     * 登录
     *
     * @param loginSO
     * @return
     */
    public ResultMessage login(LoginSO loginSO) {
        AdminInfo adminInfo = adminInfoMapper.selectByAccount(loginSO.getAccount());
        if (adminInfo != null) {
            if (MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(loginSO.getPwd()))).equals(adminInfo.getLoginPwd())) {
                adminInfo.setLoginTime(new Date());
                adminInfoMapper.updateByPrimaryKeySelective(adminInfo);

                /** 缓存用户登录信息到redis **/
                Map<String, Object> map = new HashMap<>();
                String str = CommonUtil.getRandomStr(4);
                map.put("admin", adminInfo);
                map.put("str", str);
                RedisUtil.setEx(adminInfo.getAdminId(), JSONObject.fromObject(map).toString(), 10800);

                adminInfo.setToken(InterceptorUtil.getToken(adminInfo.getAdminId(), str));

                List<PowerShow> list = new ArrayList<>();
                List<PowerBinding> groupingBindings = powerBindingMapper.selectGroupingByAdmin(adminInfo.getId());
                if (groupingBindings.size() > 0) {
                    List<Integer> groupingIds = groupingBindings.stream().map(pb -> pb.getGroupingId()).collect(Collectors.toList());
                    List<PowerBinding> showBindings = powerBindingMapper.selectPowerByGrouping(groupingIds, 1);
                    if (showBindings.size() > 0) {
                        List<Integer> showIds = showBindings.stream().map(pb -> pb.getBindingId()).collect(Collectors.toList());
                        list = powerShowMapper.selectByIds(showIds);
                    }
                }
                Map<String, Object> result = new HashMap<>();
                result.put("admin", adminInfo);
                result.put("showList", list);
                return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, result);
            } else {
                return new ResultMessage(ResultMessage.FAILED_CODE, "密码错误");
            }
        } else {
            return new ResultMessage(ResultMessage.FAILED_CODE, "账号错误");
        }
    }




    /**
     * 获取系统操作日志列表
     *
     * @param queryAdminLogSO
     * @return
     */
    public ResultMessage queryAdminLog(QueryAdminLogSO queryAdminLogSO) {
        PageHelper.startPage(queryAdminLogSO.getPageNum(), queryAdminLogSO.getPageSize());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("adminStr", queryAdminLogSO.getAccount());
        paramMap.put("startTime", queryAdminLogSO.getStartTime());
        paramMap.put("entTime", queryAdminLogSO.getEndTime());

        Page<AdminLog> page = (Page<AdminLog>) adminLogMapper.selectList(paramMap);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("list", page.getResult());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }



}
