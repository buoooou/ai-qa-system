package com.ai.qa.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security安全配置类
 * 
 * 配置用户服务的安全策略：
 * 1. 密码加密器配置
 * 2. HTTP安全配置
 * 3. 认证和授权规则
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    /**
     * 密码加密器Bean
     * 
     * 使用BCrypt算法对用户密码进行加密
     * BCrypt是一种安全的哈希算法，具有以下特点：
     * 1. 自适应性：可以调整计算复杂度
     * 2. 加盐处理：每次加密都会生成不同的盐值
     * 3. 抗彩虹表攻击：即使相同密码也会产生不同的哈希值
     * 
     * @return PasswordEncoder 密码加密器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * HTTP安全配置
     * 
     * 配置哪些URL需要认证，哪些可以匿名访问
     * 当前配置为开发阶段的宽松策略，生产环境需要加强
     * 
     * @param http HttpSecurity配置对象
     * @throws Exception 配置异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护（适用于API服务）
            .csrf().disable()
            
            // 配置授权规则
            .authorizeRequests()
                // 允许健康检查接口匿名访问
                .antMatchers("/api/user/health").permitAll()
                // 允许用户注册接口匿名访问
                .antMatchers("/api/user/register").permitAll()
                // 允许用户登录接口匿名访问
                .antMatchers("/api/user/login").permitAll()
                // 允许JWT认证相关接口匿名访问
                .antMatchers("/api/auth/login").permitAll()
                .antMatchers("/api/auth/refresh").permitAll()
                .antMatchers("/api/auth/verify").permitAll()
                .antMatchers("/api/auth/profile").permitAll()
                .antMatchers("/api/auth/logout").permitAll()
                // 允许Swagger文档接口匿名访问（开发环境）
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 其他所有请求都需要认证
                .anyRequest().authenticated()
            
            .and()
            
            // 配置HTTP Basic认证（简单起见，生产环境建议使用JWT）
            .httpBasic();
    }
}
