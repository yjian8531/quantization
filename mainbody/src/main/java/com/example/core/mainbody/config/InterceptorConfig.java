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
        
        // 1. 用户模块（登录、注册、验证码等接口不需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/verify/img",              // 注册图形验证码
                        "/user/verify/login/phone",      // 获取注册邮箱验证码
                        "/user/register",                // 用户注册
                        "/user/update/img",              // 改密图形验证码
                        "/user/verify/update/phone",     // 获取改密手机验证码
                        "/user/update/pwd",              // 更新密码
                        "/user/login"                    // 用户登录
                );

        // 2. 财务模块（全部需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/finance/**");

        // 3. 收益模块（全部需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/profit/**");

        // 4. 充值模块（全部需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/deposit/**");

        // 5. 产品模块（全部需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/product/**");

        // 6. 消息模块（全部需要 token）
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/message/**");

        // ==================== 管理端拦截器（h-admin-token） ====================
        
        // 管理端模块（仅登录接口不需要 token）
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login"    // 管理员登录
                );
    }
}
