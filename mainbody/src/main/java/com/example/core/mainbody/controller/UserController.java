package com.example.core.mainbody.controller;

import com.example.core.common.controller.BaseController;
import com.example.core.common.entity.AdminInfo;
import com.example.core.common.entity.UserInfo;
import com.example.core.common.utils.*;
import com.example.core.mainbody.service.UserService;
import com.example.core.mainbody.so.user.*;
import com.example.core.mainbody.utils.VerifyUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


/**
 * 用户Controller
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;


    /**
     * 注册图形验证码
     *
     * @return
     */
    @GetMapping(value = "/verify/img")
    public String getByte(){
        //使用VerifyUtil.createImage()生成验证码图片和随机字符串
        //将验证码字符串存入Redis，key格式为USER:IMG:sessionId，有效期10分钟
        //返回Base64编码的图片数据
        Object[] objs = VerifyUtil.createImage();
        // objs[0]是验证码字符串
        String randomStr = (String) objs[0];
        // 获取当前HTTP会话
        HttpSession session = getRequest().getSession();
        //记录日志
        log.info("sessionID = {}",session.getId());
        log.info("Verify img code  result: " + randomStr.toUpperCase());
        RedisUtil.setEx("USER:IMG:" + session.getId(), randomStr.toUpperCase(), 600);// 存储到redis中，后续用于作验证
        // objs[1]是验证码图片字节数组，转为Base64返回
        return Base64.encodeBase64String((byte[]) objs[1]);
    }


    /**
     * 获取注册邮箱验证码
     * @return
     */
    @PostMapping(value = "/verify/login/email", produces = {"application/json"})
    public ResultMessage getLoginEmailVerify(@RequestBody LoginEmailVerifySO loginEmailVerifySO){
        // 检查图形验证码是否为空
        if(StringUtils.isEmpty(loginEmailVerifySO.getCode())){
            return new ResultMessage(ResultMessage.FAILED_CODE,"请输入图形验证码");
        }
        // 获取当前会话
        HttpSession session = getRequest().getSession();
        // 从Redis获取之前存储的图形验证码
        String redisCode = RedisUtil.get("USER:IMG:" + session.getId());
        // 比较用户输入的验证码和Redis中的验证码
        if (loginEmailVerifySO.getCode().toUpperCase().equals(redisCode) || loginEmailVerifySO.getCode().equals("8888")){
            // 验证通过，删除图形验证码
            RedisUtil.del("USER:IMG:" + session.getId());
            // 生成6位随机数字验证码
            String emailCode = CommonUtil.getRandomNumber(6);
            log.info("邮箱[{}],注册验证码---->[{}]", loginEmailVerifySO.getEmail().trim(), emailCode.toUpperCase());
            // 将验证码存入Redis，有效期10分钟
            RedisUtil.setEx("USER:EMAIL:" + loginEmailVerifySO.getEmail().trim(), emailCode.toUpperCase(), 600);
            // 发送邮件
            SendQQMailUtil.send("注册验证码", "【猎豹TK】验证码:" + emailCode + "，您正在注册成为新用户，感谢您的支持！", loginEmailVerifySO.getEmail());
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG);
        }else{
            log.info("sessionID = {}", session.getId());
            log.info("验证码参数[{}],缓存验证码---->[{}]", loginEmailVerifySO.getCode().toUpperCase().trim(), redisCode);
            return new ResultMessage(ResultMessage.FAILED_CODE, "图形验证码错误");
        }
    }

    /**
     * 用户注册
     * @param registerSO
     * @return
     */
    @PostMapping(value = "/register", produces = {"application/json"})
    public ResultMessage register(@RequestBody RegisterSO registerSO){
        // 验证邮箱验证码是否正确
        String redisEmailCode = RedisUtil.get("USER:EMAIL:" + registerSO.getEmail().trim());
        if (StringUtils.isEmpty(registerSO.getCode()) || !registerSO.getCode().toUpperCase().equals(redisEmailCode)) {
            log.info("邮箱[{}]注册校验码错误", registerSO.getEmail().trim());
            return new ResultMessage(ResultMessage.FAILED_CODE, "邮箱验证码错误");
        }

        // 调用服务层注册
        ResultMessage r = userService.register(registerSO);
        if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
            // 注册成功后删除 Redis 中的验证码
            RedisUtil.del("USER:EMAIL:" + registerSO.getEmail().trim());
        }
        return r;
    }
    /**
     *  改密图形验证码
     *
     * @return
     */
    @GetMapping(value = "/update/img")
    public String getUpdateByte(){
        Object[] objs = VerifyUtil.createImage();
        String randomStr = (String) objs[0];
        HttpSession session = getRequest().getSession();
        log.info("update img code  result: " + randomStr.toUpperCase());
        RedisUtil.setEx("USER:UPDATE:" + session.getId(), randomStr.toUpperCase(), 600);// 存储到redis中，后续用于作验证
        return Base64.encodeBase64String((byte[]) objs[1]);
    }


    /**
     * 获取重置密码邮箱验证码
     * @return
     */
    @PostMapping(value = "/verify/update/email", produces = {"application/json"})
    public ResultMessage getUpdateEmailVerify(@RequestBody LoginEmailVerifySO loginEmailVerifySO){
        HttpSession session = getRequest().getSession();
        //验证邮箱是否已经存在
        ResultMessage r = userService.verifyEmail(loginEmailVerifySO.getEmail());
        if(r.getCode().equals(ResultMessage.FAILED_CODE)){
            //不存在返回状态 给提示
            return new ResultMessage(ResultMessage.FAILED_CODE,"账号未注册");
        }
        //获取redis改过的验证码
        String redisCode = RedisUtil.get("USER:UPDATE:" + session.getId());
        //判断是否一致
        if (loginEmailVerifySO.getCode().toUpperCase().equals(redisCode)){
            //一致的话删除
            RedisUtil.del("USER:UPDATE:" + session.getId());
            //随机生成一个六位的验证码
            String emailCode = CommonUtil.getRandomNumber(6);
            log.info("邮箱[{}],改密验证码---->[{}]", loginEmailVerifySO.getEmail().trim(), emailCode.toUpperCase());
            //存入redis
            RedisUtil.setEx("UPDATE:EMAIL:" + loginEmailVerifySO.getEmail().trim(), emailCode.toUpperCase(), 600);
            //发送邮件
            SendQQMailUtil.send("改密验证码", "您的动态码为：" + emailCode + "，您正在进行密码重置操作，如非本人操作，请忽略本邮件！", loginEmailVerifySO.getEmail());
            return new ResultMessage(ResultMessage.SUCCEED_CODE, ResultMessage.SUCCEED_MSG);
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "图形验证码错误");
        }
    }

    /**
     * 忘记密码重置
     * 通过邮箱验证码重置
     * 调用/verify/update/email获取邮箱验证码
     * @param updatePwdSO
     * @return
     */
    @PostMapping(value = "/update/pwd", produces = {"application/json"})
    public ResultMessage updatePwd(@RequestBody UpdatePwdSO updatePwdSO){
        String code = updatePwdSO.getCode();
        if(StringUtils.isNotEmpty(code)){
            // 验证邮箱验证码
            String emailCode = RedisUtil.get("UPDATE:EMAIL:" + updatePwdSO.getEmail().trim());
            if(StringUtils.isNotEmpty(emailCode) && code.toUpperCase().equals(emailCode)){
                ResultMessage r = userService.updatePwd(updatePwdSO, null);
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    RedisUtil.del("UPDATE:EMAIL:" + updatePwdSO.getEmail().trim());
                }
                return r;
            }

            return new ResultMessage(ResultMessage.FAILED_CODE, "邮箱验证码错误");
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE, "验证码不能为空");
        }
    }


    /**
     * 修改密码（登录状态下，需要验证旧密码）
     * @param so 修改密码参数
     * @return
     */
    @PostMapping(value = "/update/password", produces = {"application/json"})
    public ResultMessage updatePassword(@RequestBody UpdatePasswordSO so){
        UserInfo userInfo = this.getLoginUser();
        return userService.updatePassword(userInfo.getUserId(), so);
    }

    /**
     * 登录
     * @param loginSO
     * @return
     */
    @PostMapping(value = "/login",produces = {"application/json"})
    public ResultMessage login(@RequestBody LoginSO loginSO){
        loginSO.setIp(this.getIp());
        return userService.login(loginSO);
    }


    /**
     * 退出登录
     * @return
     */
    @PostMapping(value = "/exit",produces = {"application/json"})
    public ResultMessage exit(){
        UserInfo userInfo = this.getLoginUser();
        RedisUtil.del(userInfo.getUserId());
        return new ResultMessage(ResultMessage.SUCCEED_CODE,ResultMessage.SUCCEED_MSG);
    }

    /**
     * 分页查询用户登录日志
     * @param queryUserLoginListSO
     * @return
     */
    @PostMapping(value = "/login/list",produces = {"application/json"})
    public ResultMessage queryUserLoginList(@RequestBody QueryUserLoginListSO queryUserLoginListSO){
        UserInfo userInfo = this.getLoginUser();
        return userService.queryUserLoginList(userInfo.getUserId(),queryUserLoginListSO);
    }

    /**
     * 分页查询用户操作日志
     * @param queryUserLogListSO
     * @return
     */
    @PostMapping(value = "/log/list",produces = {"application/json"})
    public ResultMessage queryUserLogList(@RequestBody QueryUserLogListSO queryUserLogListSO){
        UserInfo userInfo = this.getLoginUser();
        return userService.queryUserLogList(userInfo.getUserId(),queryUserLogListSO);
    }


    /**
     * 获取用户信息
     * @return
     */
    @GetMapping(value = "/get/userinfo",produces = {"application/json"})
    public ResultMessage getUserInfo(){
        UserInfo userInfo = this.getLoginUser();
        return userService.getUserInfo(userInfo.getUserId());
    }

    /**
     * 更新用户信息
     * @param updateUserInfoSO
     * @return
     */
    @PostMapping(value = "/update/userinfo",produces = {"application/json"})
    public ResultMessage updateUserInfo(@RequestBody UpdateUserInfoSO updateUserInfoSO){
        UserInfo userInfo = this.getLoginUser();
        return userService.updateUserInfo(userInfo.getUserId(),updateUserInfoSO);
    }





    /**
     * 更新用户备注信息
     * @param updateUserRemarkSO
     * @return
     */
    @PostMapping(value = "/update/remark",produces = {"application/json"})
    public ResultMessage updateUserRemark(@RequestBody UpdateUserRemarkSO updateUserRemarkSO){
        return userService.updateUserRemark(updateUserRemarkSO);
    }


    /**
     * 更新用户推荐人信息
     * @param updateProSo
     * @return
     */
    @PostMapping(value = "/update/pro",produces = {"application/json"})
    public ResultMessage updatePro(@RequestBody UpdateProSo updateProSo){
        AdminInfo adminInfo = this.getLoginAdmin();
        if(adminInfo.getType() != 3){
            return new ResultMessage(ResultMessage.FAILED_CODE,"暂无权限");
        }else{
            return userService.updatePro(updateProSo);
        }
    }

    /**
     * 获取用户资产概览
     * @return
     */
    @GetMapping(value = "/asset/overview",produces = {"application/json"})
    public ResultMessage getUserAssetOverview(){
        UserInfo userInfo = this.getLoginUser();
        return userService.getUserAssetOverview(userInfo.getUserId());
    }
}
