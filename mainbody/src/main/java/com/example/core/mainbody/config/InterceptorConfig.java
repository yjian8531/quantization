package com.example.core.mainbody.config;

import com.example.core.mainbody.filter.AdminInterceptor;
import com.example.core.mainbody.filter.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置类
 * 注册用户端和管理端拦截器，定义需要拦截的路径和排除路径
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private UserInterceptor userInterceptor;
    
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        // ==================== 用户端拦截器（h-user-token） ====================
        // 用户模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/verify/img",              // 注册图形验证码
                        "/user/verify/login/email",      // 获取注册邮箱验证码
                        "/user/register",                // 用户注册
                        "/user/update/img",              // 改密图形验证码
                        "/user/verify/update/email",     // 获取改密手机验证码
                        "/user/update/pwd",              // 更新密码
                        "/user/login"                    // 用户登录
                );

        //财务模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/finance/**");

        //订单模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/order/**")
                .excludePathPatterns(
                        "/order/trade",                   // 接收交易日志
                        "/order/position",                // 接收仓位日志
                        "/order/symbol/list",             // 获取币对列表
                        "/order/robot/public/list",        // 查询公开机器人列表
                        "/order/profit/curve",             // 查询机器人收益曲线
                        "/order/position/history",        // 查询机器人历史仓位
                        "/order/trade/record",            // 查询机器人交易记录
                        "/order/robot/public/detail",      // 查询公开机器人详细
                        "/order/check/param",                // 策略参数风控评估
                        "/order/update/income",                // 更新订单收益、收益率及年化率
                        "/order/status"                     //接收策略心跳机制推送
                );
        //收益模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/profit/**");

        //充值模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/deposit/**");

        // 产品模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/product/**")
                .excludePathPatterns(
                        "/product/list",                 // 产品列表
                        "/product/detail",               // 产品详情
                        "/product/admin/list",           // 产品列表-管理后台
                        "/product/symbol/list",          // 获取币对列表
                        "/product/summary",               // 平台首页汇总数据
                        "/product/param/config"          // 查询产品配置参数信息
                );


        //消息模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/message/**");

        // 6. 钱包模块（全部需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/wallet/**");


        // 钱包模块
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/wallet/**");
        // ==================== 管理端拦截器（h-admin-token） ====================
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login"    // 管理员登录
                );
    }
}
