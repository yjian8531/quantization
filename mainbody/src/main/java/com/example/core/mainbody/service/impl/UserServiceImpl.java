package com.example.core.mainbody.service.impl;

import com.example.core.common.entity.*;
import com.example.core.common.mapper.*;
import com.example.core.common.utils.*;
import com.example.core.mainbody.service.UserService;
import com.example.core.mainbody.so.finance.UserAssetOverviewVO;
import com.example.core.mainbody.so.user.*;
import com.example.core.mainbody.utils.InterceptorUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.core.common.entity.UserDiscount;
import java.math.BigDecimal;
import java.util.*;

/**
 * 用户业务实现类
 */
@Slf4j
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserFinanceMapper userFinanceMapper;

//    @Autowired
//    private MessageInfoMapper messageInfoMapper;
//
//    @Autowired
//    private ProductInfoMapper productInfoMapper;
//
    @Autowired
    private UserDiscountMapper userDiscountMapper;

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Autowired
    private UserLogMapper userLogMapper;

    @Autowired
    private UserWxMapper userWxMapper;

    @Autowired
    private UserProMapper userProMapper;

//    @Autowired
//    private AreaNodeMapper areaNodeMapper;
//
//    @Autowired
//    private MakePriceMapper makePriceMapper;

    @Autowired
    private PowerBindingMapper powerBindingMapper;

    @Autowired
    private FinanceDetailMapper userFinanceDetailMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;


//    @Autowired
//    private AdminPromotionAccountMapper adminPromotionAccountMapper;
//
//    @Autowired
//    private ChatInfoMapper chatInfoMapper;


    /**
     * 用户注册
     *
     * @param registerSO
     * @return
     */
//    @Transactional(rollbackFor = Exception.class)
//    public ResultMessage register(RegisterSO registerSO) {
//        //注册时查询一遍该邮箱
//        UserInfo userInfo = userInfoMapper.selectByPhone(registerSO.getEmail());
//        //若不为空
//        if (userInfo != null) {
//            //提示邮箱已经被注册
//            return new ResultMessage(ResultMessage.FAILED_CODE, "当前邮箱已注册");
//        }
//        //查询手机号
//        userInfo = userInfoMapper.selectByPhone(registerSO.getPhone());
//        //若不为空
//        if (userInfo != null) {
//            //提示手机号已经被注册
//            return new ResultMessage(ResultMessage.FAILED_CODE, "当前手机已注册");
//        }
//
//        /** 用户填写推广码注册 **/
//        UserInfo proUser = null;
//        if (StringUtils.isNotEmpty(registerSO.getMarket())) {
//            proUser = queryMarket(registerSO.getMarket());
//            if (proUser == null) {
//                return new ResultMessage(ResultMessage.FAILED_CODE, "无效推广码");
//            }
//        }
//
//        userInfo = new UserInfo();
//        userInfo.setUserId(CommonUtil.getRandomStr(32));
//        userInfo.setAvatar(registerSO.getAvatar()); // 使用用户上传的头像
//        userInfo.setAccount(registerSO.getEmail());
//        userInfo.setNickName(registerSO.getName()); // 使用用户输入的昵称
//        userInfo.setPhone(registerSO.getPhone());
//        userInfo.setEmail(registerSO.getEmail());
//        String marketNew = CommonUtil.getRandomStr(6).toUpperCase();
//        /** 校验推广码是否存在 **/
//        UserInfo marketUser = queryMarket(marketNew);
//        while (marketUser != null) {
//            marketNew = CommonUtil.getRandomStr(6).toUpperCase();
//            marketUser = queryMarket(marketNew);
//        }
//        userInfo.setMarket(marketNew);
//        userInfo.setNickName(registerSO.getName());
//        userInfo.setLoginPwd(MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(registerSO.getPwd()))));
//        userInfo.setStatus(CommonUtil.STATUS_0);
//        userInfo.setType(CommonUtil.STATUS_0);
//        userInfo.setLoginIp(registerSO.getIp());
//        userInfo.setMarket(CommonUtil.getRandomStr(6).toUpperCase());
//        userInfo.setUpdateTime(new Date());
//        userInfo.setCreateTime(new Date());
//        int i = userInfoMapper.insertSelective(userInfo);
//        if (i > 0) {
//
//            if (StringUtils.isNotEmpty(registerSO.getMarket())) {
//                /** 添加推广信息 **/
//                UserPro userPro = new UserPro();
//                userPro.setUserId(userInfo.getUserId());
//                userPro.setProUserId(proUser.getUserId());
//                userPro.setStatus(CommonUtil.STATUS_0);
//                userPro.setCreateTime(new Date());
//                userProMapper.insertSelective(userPro);
//            }
//
//            if (StringUtils.isNotEmpty(registerSO.getOpenId())) {
//                /** 添加微信绑定信息 **/
//                UserWx userWx = new UserWx();
//                userWx.setUserId(userInfo.getUserId());
//                userWx.setOpenId(registerSO.getOpenId());
//                userWx.setStatus(CommonUtil.STATUS_0);
//                userWx.setCreateTime(new Date());
//                userWxMapper.insertSelective(userWx);
//            }
//
//            //创建用户财务信息
//            UserFinance userFinance = new UserFinance();
//            userFinance.setUserId(userInfo.getUserId());
//            userFinance.setType(CommonUtil.STATUS_0);
//            userFinanceMapper.insertSelective(userFinance);
//
//
//            /** 缓存用户登录信息到redis **/
//            Map<String, Object> map = new HashMap<>();
//            String str = CommonUtil.getRandomStr(4);
//            map.put("userInfo", userInfo);
//            map.put("str", str);
//            RedisUtil.setEx(userInfo.getUserId(), JSONObject.fromObject(map).toString(), 10800);
//            userInfo.setToken(InterceptorUtil.getToken(userInfo.getUserId(), str));
//
//            return new ResultMessage(ResultMessage.SUCCEED_CODE, "注册成功", userInfo);
//        } else {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "注册失败");
//        }
//
//    }

    /**
     *
     * 用户注册
     * @param registerSO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage register(RegisterSO registerSO) {
        // 1. 校验推广码
        if (StringUtils.isEmpty(registerSO.getMarket())) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "必须填写推广码才能注册");
        }
        UserInfo proUser = queryMarket(registerSO.getMarket());
        if (proUser == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "无效推广码");
        }

        // 2. 检查邮箱是否已注册
        if (userInfoMapper.selectByEmail(registerSO.getEmail()) != null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "当前邮箱已注册");
        }

        // 3. 组装用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(CommonUtil.getRandomStr(32));
        userInfo.setAvatar(registerSO.getAvatar());
        userInfo.setAccount(registerSO.getEmail());
        userInfo.setNickName(registerSO.getName());
        userInfo.setEmail(registerSO.getEmail());
        userInfo.setLoginPwd(MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(registerSO.getPwd()))));

        // 生成唯一推广码
        String marketNew = CommonUtil.getRandomStr(6).toUpperCase();
        while (queryMarket(marketNew) != null) {
            marketNew = CommonUtil.getRandomStr(6).toUpperCase();
        }
        userInfo.setMarket(marketNew);
        userInfo.setStatus(CommonUtil.STATUS_0);
        userInfo.setType(registerSO.getType() != null ? registerSO.getType() : CommonUtil.STATUS_0);
        userInfo.setUpdateTime(new Date());
        userInfo.setCreateTime(new Date());

        // 4. 插入用户表
        int i = userInfoMapper.insertSelective(userInfo);
        if (i > 0) {
            // 添加推广关系
            UserPro userPro = new UserPro();
            userPro.setUserId(userInfo.getUserId());
            userPro.setProUserId(proUser.getUserId());
            userPro.setStatus(CommonUtil.STATUS_0);
            userPro.setCreateTime(new Date());
            userProMapper.insertSelective(userPro);

            // 创建财务账户
            UserFinance userFinance = new UserFinance();
            userFinance.setUserId(userInfo.getUserId());
            userFinance.setType(CommonUtil.STATUS_0);
            userFinanceMapper.insertSelective(userFinance);

            // 缓存登录信息到 Redis
            Map<String, Object> map = new HashMap<>();
            String str = CommonUtil.getRandomStr(4);
            map.put("userInfo", userInfo);
            map.put("str", str);
            RedisUtil.setEx(userInfo.getUserId(), JSONObject.fromObject(map).toString(), 10800);
            userInfo.setToken(InterceptorUtil.getToken(userInfo.getUserId(), str));

            return new ResultMessage(ResultMessage.SUCCEED_CODE, "注册成功", userInfo);
        }
        return new ResultMessage(ResultMessage.FAILED_CODE, "注册失败");
    }

    /**
     * 跟进推广码查询用户信息
     *
     * @param market
     * @return
     */
    private UserInfo queryMarket(String market) {
        UserInfo userInfo = userInfoMapper.selectByMarket(market);
        return userInfo;
    }

    /**
     * 验证邮箱是否存在
     *
     * @param email
     * @return
     */
    public ResultMessage verifyEmail(String email) {
        UserInfo userInfo = userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "邮箱不存在");
        } else {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "邮箱存在");
        }
    }


    /**
     * 修改密码
     *
     * @param updatePwdSO
     * @return
     */
    public ResultMessage updatePwd(UpdatePwdSO updatePwdSO, String openId) {
        UserInfo userInfo = userInfoMapper.selectByEmail(updatePwdSO.getEmail());
        if (userInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "当前账号不存在");
        }
        if (openId != null) {
            UserWx userWx = userWxMapper.selectByUserId(userInfo.getUserId());
            if (userWx == null || userWx.getOpenId().equals(openId)) {
                return new ResultMessage(ResultMessage.FAILED_CODE, "改密校验码错误");
            }
        }


        UserInfo entity = new UserInfo();
        entity.setId(userInfo.getId());
        entity.setLoginPwd(MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(updatePwdSO.getNewPwd()))));
        entity.setUpdateTime(new Date());
        int i = userInfoMapper.updateByPrimaryKeySelective(entity);
        if (i > 0) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "修改成功");
        } else {
            return new ResultMessage(ResultMessage.FAILED_CODE, "修改失败");
        }
    }


    /**
     * 修改密码（登录状态下，需要验证旧密码）
     *
     * @param userId 用户ID
     * @param so 修改密码参数
     * @return 操作结果
     */
    @Override
    public ResultMessage updatePassword(String userId, UpdatePasswordSO so) {
        // 1. 校验旧密码是否为空
        if (StringUtils.isEmpty(so.getOldPwd())) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "请输入旧密码");
        }

        // 2. 校验新密码是否为空
        if (StringUtils.isEmpty(so.getNewPwd())) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "请输入新密码");
        }

        // 3. 查询用户信息
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "用户不存在");
        }

        // 4. 验证旧密码是否正确（三重MD5加密）
        String encryptedOldPwd = MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(so.getOldPwd())));
        if (!encryptedOldPwd.equals(userInfo.getLoginPwd())) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "旧密码错误");
        }

        // 5. 更新新密码
        UserInfo entity = new UserInfo();
        entity.setId(userInfo.getId());
        entity.setLoginPwd(MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(so.getNewPwd()))));
        entity.setUpdateTime(new Date());

        int result = userInfoMapper.updateByPrimaryKeySelective(entity);
        if (result > 0) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "密码修改成功，请重新登录");
        } else {
            return new ResultMessage(ResultMessage.FAILED_CODE, "密码修改失败");
        }
    }


//    public ResultMessage login(LoginSO loginSO) {
//        // 根据邮箱查询用户（原来是查手机号）
//        UserInfo userInfo = userInfoMapper.selectByEmail(loginSO.getEmail());
//        if (userInfo != null) {
//            if (userInfo.getStatus() == 1) {
//                return new ResultMessage(ResultMessage.FAILED_CODE, "当前账号已被禁用");
//            }
//
//            UserLogin userLogin = new UserLogin();
//            userLogin.setCreateTime(new Date());
//            userLogin.setUserId(userInfo.getUserId());
//            userLogin.setLoginIp(loginSO.getIp());
//
//            // 密码验证
//            if (MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(loginSO.getPwd()))).equals(userInfo.getLoginPwd())) {
//                Map<String, Object> map = new HashMap<>();
//                String str = CommonUtil.getRandomStr(4);
//                map.put("userInfo", userInfo);
//                map.put("str", str);
//                RedisUtil.setEx(userInfo.getUserId(), JSONObject.fromObject(map).toString(), 10800);
//
//                userInfo.setToken(InterceptorUtil.getToken(userInfo.getUserId(), str));
//                userInfo.setLoginIp(loginSO.getIp());
//                userInfo.setLoginTime(new Date());
//                userInfoMapper.updateByPrimaryKeySelective(userInfo);
//                userLogin.setStatus(CommonUtil.STATUS_0);
//                userLoginMapper.insertSelective(userLogin);
//
//                UserWx userWx = userWxMapper.selectByUserId(userInfo.getUserId());
//                if (userWx == null) {
//                    userInfo.setWx(0);
//                } else {
//                    userInfo.setWx(1);
//                }
//                return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, userInfo);
//            } else {
//                userLogin.setStatus(CommonUtil.STATUS_1);
//                userLoginMapper.insertSelective(userLogin);
//                return new ResultMessage(ResultMessage.FAILED_CODE, "密码错误");
//            }
//        } else {
//            return new ResultMessage(ResultMessage.FAILED_CODE, "邮箱账号错误");
//        }
//    }

    // ... existing code ...
    @Transactional(rollbackFor = Exception.class)
    public ResultMessage login(LoginSO loginSO) {
        // 1. 根据邮箱查询用户
        UserInfo userInfo = userInfoMapper.selectByEmail(loginSO.getEmail());
        if (userInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "邮箱账号错误");
        }

        // 2. 检查账号状态
        if (userInfo.getStatus() == 1) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "当前账号已被禁用");
        }

        // 3. 密码验证
        String inputPwd = MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(loginSO.getPwd())));
        if (!inputPwd.equals(userInfo.getLoginPwd())) {
            // 记录登录失败日志
            UserLogin failLog = new UserLogin();
            failLog.setCreateTime(new Date());
            failLog.setUserId(userInfo.getUserId());
            failLog.setLoginIp(loginSO.getIp());
            failLog.setStatus(CommonUtil.STATUS_1);
            userLoginMapper.insertSelective(failLog);
            return new ResultMessage(ResultMessage.FAILED_CODE, "密码错误");
        }

        // 4. 密码正确，生成 Token 并更新登录信息
        String randomStr = CommonUtil.getRandomStr(4);
        Map<String, Object> redisMap = new HashMap<>();
        redisMap.put("userInfo", userInfo);
        redisMap.put("str", randomStr);
        RedisUtil.setEx(userInfo.getUserId(), JSONObject.fromObject(redisMap).toString(), 604800);//登录缓存7天

        // 生成 Token
        String token = InterceptorUtil.getToken(userInfo.getUserId(), randomStr);
        userInfo.setToken(token);
        userInfo.setLoginIp(loginSO.getIp());
        userInfo.setLoginTime(new Date());

        // 更新用户最后登录信息
        userInfoMapper.updateByPrimaryKeySelective(userInfo);

        // 记录登录成功日志
        UserLogin successLog = new UserLogin();
        successLog.setCreateTime(new Date());
        successLog.setUserId(userInfo.getUserId());
        successLog.setLoginIp(loginSO.getIp());
        successLog.setStatus(CommonUtil.STATUS_0);
        userLoginMapper.insertSelective(successLog);

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, userInfo);
    }
// ... existing code ...



    /**
     * 分页查询用户登录日志
     *
     * @param userId
     * @param queryUserLoginListSO
     * @return
     */
    public ResultMessage queryUserLoginList(String userId, QueryUserLoginListSO queryUserLoginListSO) {
        PageHelper.startPage(queryUserLoginListSO.getPageNum(), queryUserLoginListSO.getPageSize());
        Page<UserLogin> page = (Page<UserLogin>) userLoginMapper.selectList(userId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("list", page.getResult());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }

    /**
     * 分页查询用户操作日志
     *
     * @param userId
     * @param queryUserLogListSO
     * @return
     */
    public ResultMessage queryUserLogList(String userId, QueryUserLogListSO queryUserLogListSO) {
        PageHelper.startPage(queryUserLogListSO.getPageNum(), queryUserLogListSO.getPageSize());

        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        param.put("alias", queryUserLogListSO.getAlias());
        Page<UserLog> page = (Page<UserLog>) userLogMapper.selectList(param);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("list", page.getResult());
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, resultMap);
    }


    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public ResultMessage getUserInfo(String userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setLoginPwd(null);
        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, userInfo);
    }

    /**
     * 更新用户信息
     *
     * @param userId
     * @param updateUserInfoSO
     * @return
     */
    public ResultMessage updateUserInfo(String userId, UpdateUserInfoSO updateUserInfoSO) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (MD5.MD5Encode(MD5.MD5Encode(MD5.MD5Encode(updateUserInfoSO.getLoginPwd()))).equals(userInfo.getLoginPwd())) {

            userInfo.setNickName(updateUserInfoSO.getNickName());
            userInfo.setPhone(updateUserInfoSO.getPhone());
            int i = userInfoMapper.updateByPrimaryKeySelective(userInfo);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "修改成功");
            } else {
                return new ResultMessage(ResultMessage.FAILED_CODE, "修改失败");
            }
        } else {
            return new ResultMessage(ResultMessage.FAILED_CODE, "密码校验失败");
        }
    }

    /**
     * 获取用户折扣
     *
     * @param nodeId 节点ID
     * @param type   类型
     * @param client 客户端
     * @param userId 用户Id
     * @return
     */
    public BigDecimal getDiscount(Integer nodeId, Integer type, String client, String userId) {
        List<UserDiscount> list = userDiscountMapper.selectByUserId(userId);
        BigDecimal discount = UserDiscountUtil.get(nodeId, type, client, list);
        return discount;
    }




    /**
     * 更新用户备注信息
     *
     * @param updateUserRemarkSO
     * @return
     */
    public ResultMessage updateUserRemark(UpdateUserRemarkSO updateUserRemarkSO) {
        UserInfo userInfo = userInfoMapper.selectById(updateUserRemarkSO.getUserId());
        userInfo.setRemark(updateUserRemarkSO.getRemar());
        userInfo.setUpdateTime(new Date());
        int i = userInfoMapper.updateByPrimaryKeySelective(userInfo);
        if (i > 0) {
            return new ResultMessage(ResultMessage.SUCCEED_CODE, "更新成功");
        } else {
            return new ResultMessage(ResultMessage.FAILED_CODE, "更新失败");
        }
    }

    /**
     * 更新用户推荐人信息
     *
     * @param updateProSo
     * @return
     */
    public ResultMessage updatePro(UpdateProSo updateProSo) {
        UserInfo proUser = userInfoMapper.selectByPhone(updateProSo.getProAccount());
        if (proUser == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "推荐人信息不存在");
        }

        UserPro userPro = userProMapper.selectByUserId(updateProSo.getUserId());
        if (userPro == null) {
            userPro = new UserPro();
            userPro.setUserId(updateProSo.getUserId());
            userPro.setProUserId(proUser.getUserId());
            userPro.setStatus(0);
            userPro.setCreateTime(new Date());
            int i = userProMapper.insertSelective(userPro);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "更新成功");
            } else {
                return new ResultMessage(ResultMessage.FAILED_CODE, "更新失败");
            }
        } else {
            userPro.setProUserId(proUser.getUserId());
            userPro.setStatus(0);
            userPro.setCreateTime(new Date());
            int i = userProMapper.updateByPrimaryKeySelective(userPro);
            if (i > 0) {
                return new ResultMessage(ResultMessage.SUCCEED_CODE, "更新成功");
            } else {
                return new ResultMessage(ResultMessage.FAILED_CODE, "更新失败");
            }
        }
    }

    /**
     * 获取用户资产总览
     *
     * @param userId
     * @return
     */
    @Override
    public ResultMessage getUserAssetOverview(String userId) {
        // 查询用户信息
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (userInfo == null) {
            return new ResultMessage(ResultMessage.FAILED_CODE, "用户不存在");
        }

        // 查询用户财务信息
        UserFinance userFinance = userFinanceMapper.selectByUserId(userId);
        if (userFinance == null) {
            // 如果没有财务信息，创建一条默认记录
            userFinance = new UserFinance();
            userFinance.setUserId(userId);
            userFinance.setTotalNum(BigDecimal.ZERO);
            userFinance.setValidNum(BigDecimal.ZERO);
            userFinance.setFrozenNum(BigDecimal.ZERO);
            userFinance.setType(CommonUtil.STATUS_0);
            userFinance.setCreateTime(new Date());
            userFinance.setUpdateTime(new Date());
            userFinanceMapper.insertSelective(userFinance);
        }

        // 从策略机器人订单表查询用户所有机器人的累计交易收益
        BigDecimal totalProfit = orderInfoMapper.selectTotalIncomeByUserId(userId);
        if (totalProfit == null) {
            totalProfit = BigDecimal.ZERO;
        }

        // 构造返回数据
        UserAssetOverviewVO vo = new UserAssetOverviewVO();
        vo.setTotalAsset(userFinance.getTotalNum() != null ? userFinance.getTotalNum() : BigDecimal.ZERO);
        vo.setTotalProfit(totalProfit);  // 使用真实的累计收益
        vo.setValidNum(userFinance.getValidNum() != null ? userFinance.getValidNum() : BigDecimal.ZERO);
        vo.setFrozenNum(userFinance.getFrozenNum() != null ? userFinance.getFrozenNum() : BigDecimal.ZERO);

        // VIP状态：根据type字段判断，type=1表示VIP
        vo.setIsVip(userInfo.getType() != null && userInfo.getType() == 1 ? 1 : 0);

        // 账号名称：优先使用昵称，没有则使用邮箱前缀
        if (StringUtils.isNotEmpty(userInfo.getNickName())) {
            vo.setAccountName(userInfo.getNickName());
        } else if (StringUtils.isNotEmpty(userInfo.getEmail())) {
            String email = userInfo.getEmail();
            vo.setAccountName(email.substring(0, email.indexOf("@")));
        } else {
            vo.setAccountName("投资者");
        }

        // 账号ID：使用userId后6位
        vo.setAccountId(userInfo.getUserId().substring(userInfo.getUserId().length() - 6));

        return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG, vo);
    }

    /**
     * 计算折扣率
     * @param originalPrice 原始价格
     * @return 折扣率
     */
    private BigDecimal calculateDiscountRate(BigDecimal originalPrice) {
        BigDecimal discount = calculateDiscountPrice(originalPrice);
        // 将折扣率乘以10并保留四位小数
        return discount.multiply(new BigDecimal("10")).divide(originalPrice, 4, BigDecimal.ROUND_HALF_UP);
    }
    /**
     * 计算折扣价格
     * @param originalPrice 原始价格
     * @return 折扣后的价格
     */
    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice) {
        // 折扣规则表
        BigDecimal discount = BigDecimal.ONE;

        switch (originalPrice.intValue()) {
            case 50:
                discount = new BigDecimal("0.9");
                break;
            case 100:
                discount = new BigDecimal("0.7");
                break;
            case 120:
                discount = new BigDecimal("0.75");
                break;
            case 150:
                discount = new BigDecimal("0.8");
                break;
            case 210:
                discount = new BigDecimal("0.857");
                break;
            case 258:
                discount = new BigDecimal("0.864");
                break;
            case 360:
                discount = new BigDecimal("0.83");
                break;
            case 502:
                discount = new BigDecimal("0.928");
                break;
            case 598:
                discount = new BigDecimal("0.83");
                break;
            case 980:
                discount = new BigDecimal("0.816");
                break;
            default:
                discount = new BigDecimal("0.9"); // 默认折扣
                break;
        }
        // 计算折扣后的价格
        return originalPrice.multiply(discount).setScale(4, BigDecimal.ROUND_HALF_UP);
    }
}
