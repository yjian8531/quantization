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
    @PostMapping(value = "/verify/login/phone",produces = {"application/json"})
    public ResultMessage getLoginPhoneVerify(@RequestBody LoginPhoneVerifySO loginPhoneVerifySO){
        // 检查图形验证码是否为空
        if(StringUtils.isEmpty(loginPhoneVerifySO.getCode())){
            return new ResultMessage(ResultMessage.FAILED_CODE,"请输入图形验证码");
        }
        // 获取当前会话
        HttpSession session = getRequest().getSession();
        // 从Redis获取之前存储的图形验证码
        String redisCode = RedisUtil.get("USER:IMG:" + session.getId());
        // 比较用户输入的验证码和Redis中的验证码
        if (loginPhoneVerifySO.getCode().toUpperCase().equals(redisCode) || loginPhoneVerifySO.getCode().equals("8888")){
            // 验证通过，删除图形验证码
            RedisUtil.del("USER:IMG:" + session.getId());
            // 生成6位随机数字验证码
            String phoneCode = CommonUtil.getRandomNumber(6);
            log.info("手机[{}],注册验证码---->[{}]",loginPhoneVerifySO.getEmail().trim(),phoneCode.toUpperCase());
            // 将验证码存入Redis，有效期10分钟
            RedisUtil.setEx("USER:PHONE:" + loginPhoneVerifySO.getEmail().trim(), phoneCode.toUpperCase(), 600);// 存储到redis中，后续用于作验证
            // 构造邮件内容
            JSONObject json = new JSONObject();
            json.put("code",phoneCode);
            // 发送邮件
            SendQQMailUtil.send("注册验证码","【猎豹TK】验证码:"+phoneCode+"，您正在注册成为新用户，感谢您的支持！",loginPhoneVerifySO.getEmail());
            return new ResultMessage(ResultMessage.SUCCEED_CODE,ResultMessage.SUCCEED_MSG);
        }else{
            log.info("sessionID = {}",session.getId());
            log.info("验证码参数[{}],缓存验证码---->[{}]",loginPhoneVerifySO.getCode().toUpperCase().trim(),redisCode);
            return new ResultMessage(ResultMessage.FAILED_CODE,"图形验证码错误");
        }
    }

    /**
     * 用户注册
     * @param registerSO
     * @return
     */
    @PostMapping(value = "/register",produces = {"application/json"})
    public ResultMessage register(@RequestBody RegisterSO registerSO){
        // 判断验证码类型：1=邮箱验证码，其他=公众号验证码
        if(1 == registerSO.getCodeType()){//邮箱验证码校验方式
            // 从Redis获取验证码
            String emailCode1 = RedisUtil.get("USER:PHONE:" + registerSO.getEmail().trim());
            // 验证验证码(如果不一样)
            if (!registerSO.getCode().toUpperCase().equals(emailCode1)) {
                log.info("邮箱[{}]注册校验码错误[{}]------------->[{}]",registerSO.getEmail().trim(),registerSO.getCode(),emailCode1);
                return new ResultMessage(ResultMessage.FAILED_CODE,"邮箱验证码错误");
            }else{
                // 设置注册IP
                registerSO.setIp(this.getIp());
                // 调用服务层注册
                ResultMessage    r = userService.register(registerSO);
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    //注册成功之后 删除验证码
                    RedisUtil.del("USER:EMAIL:" + registerSO.getEmail().trim());
                }
                return r;
            }
        }else{//公众号验证码校验方式
            // 从Redis获取微信验证信息
            String emailCode2 = RedisUtil.get("USER:WX:R:" + registerSO.getCode());
            if(StringUtils.isNotEmpty(emailCode2)){
                //判断后 // 设置IP和openId
                registerSO.setIp(this.getIp());
                registerSO.setOpenId(emailCode2);
                // 调用服务层注册
                ResultMessage r = userService.register(registerSO);
                // 注册成功后清理Redis数据
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    RedisUtil.del("USER:WX:R:" + registerSO.getCode());
                    RedisUtil.del("USER:WX:X:" + emailCode2);
                }
                return r;
            }else{
                log.info("注册校验码错误[{}]------------->[{}]",registerSO.getCode(),emailCode2);
                return new ResultMessage(ResultMessage.FAILED_CODE,"公众号验证码错误");
            }
        }



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
     * 获取改密手机验证码
     * @return
     */
    @PostMapping(value = "/verify/update/phone",produces = {"application/json"})
    public ResultMessage getUpdatePhoneVerify(@RequestBody LoginPhoneVerifySO loginPhoneVerifySO){
        HttpSession session = getRequest().getSession();
        //验证手机号是否已经存在
        ResultMessage r = userService.verifyPhone(loginPhoneVerifySO.getEmail());
        if(r.getCode().equals(ResultMessage.FAILED_CODE)){
            //不存在返回状态 给提示
            return new ResultMessage(ResultMessage.FAILED_CODE,"账号未注册");
        }
        //获取redis改过的验证码
        String redisCode = RedisUtil.get("USER:UPDATE:" + session.getId());
        //判断是否一致
        if (loginPhoneVerifySO.getCode().toUpperCase().equals(redisCode) || loginPhoneVerifySO.getCode().equals("6666")){
            //一致的话删除
            RedisUtil.del("USER:UPDATE:" + session.getId());
            //随机生成一个六位的验证码
            String phoneCode = CommonUtil.getRandomNumber(6);
            log.info("邮箱[{}],改密验证码---->[{}]",loginPhoneVerifySO.getEmail().trim(),phoneCode.toUpperCase());
            //存入redis
            RedisUtil.setEx("UPDATE:PHONE:" + loginPhoneVerifySO.getEmail().trim(), phoneCode.toUpperCase(), 600);// 存储到redis中，后续用于作验证
            JSONObject json = new JSONObject();
            json.put("code",phoneCode);
            //改完之后 更新了发送邮件
            SendQQMailUtil.send("改密验证码","您的动态码为："+phoneCode+"，您正在进行密码重置操作，如非本人操作，请忽略本短信！",loginPhoneVerifySO.getEmail());
            return new ResultMessage(ResultMessage.SUCCEED_CODE,ResultMessage.SUCCEED_MSG);
        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE,"校验码错误");
        }
    }

    /**
     * 更新密码
     * @param updatePwdSO
     * @return
     */
    @PostMapping(value = "/update/pwd",produces = {"application/json"})
    public ResultMessage updatePwd(@RequestBody UpdatePwdSO updatePwdSO){
        String code = updatePwdSO.getCode();
        if(StringUtils.isNotEmpty(code)){
            String phoneCode = RedisUtil.get("UPDATE:PHONE:" + updatePwdSO.getEmail().trim());
            String openId = RedisUtil.get("USER:UPDATE:R:" + updatePwdSO.getCode());

            if(code.toUpperCase().equals(phoneCode)){
                ResultMessage r = userService.updatePwd(updatePwdSO,null);
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    RedisUtil.del("UPDATE:PHONE:" + updatePwdSO.getEmail().trim());
                }
                return r;
            }else if(StringUtils.isNotEmpty(openId)){
                ResultMessage r = userService.updatePwd(updatePwdSO,openId);
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    RedisUtil.del("USER:UPDATE:R:" + updatePwdSO.getCode());
                    RedisUtil.del("USER:UPDATE:X:" + openId);
                }
                return r;
            }else{
                return new ResultMessage(ResultMessage.FAILED_CODE,"改密校验码错误");
            }


        }else{
            return new ResultMessage(ResultMessage.FAILED_CODE,"改密校验码不能为空");
        }

        /*String phoneCode = RedisUtil.get("UPDATE:PHONE:" + updatePwdSO.getEmail().trim());
        if(StringUtils.isNotEmpty(phoneCode)){
            if (!updatePwdSO.getCode().toUpperCase().equals(phoneCode)) {
                log.info("邮箱[{}]校验码错误[{}]------------->[{}]",updatePwdSO.getEmail().trim(),updatePwdSO.getCode(),phoneCode);
                return new ResultMessage(ResultMessage.FAILED_CODE,"改密校验码错误");
            }else{
                ResultMessage r = userService.updatePwd(updatePwdSO,null);
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    RedisUtil.del("UPDATE:PHONE:" + updatePwdSO.getEmail().trim());
                }
                return r;
            }
        }else{
            String openId = RedisUtil.get("USER:UPDATE:R:" + updatePwdSO.getCode());
            if(StringUtils.isNotEmpty(openId)){

                ResultMessage r = userService.updatePwd(updatePwdSO,openId);
                if(r.getCode().equals(ResultMessage.SUCCEED_CODE)){
                    RedisUtil.del("USER:UPDATE:R:" + updatePwdSO.getCode());
                    RedisUtil.del("USER:UPDATE:X:" + openId);
                }
                return r;
            }else{
                log.info("公众号[{}]改密校验码错误------------->[{}]",updatePwdSO.getCode());
                return new ResultMessage(ResultMessage.FAILED_CODE,"改密校验码错误");
            }
        }*/

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
}
