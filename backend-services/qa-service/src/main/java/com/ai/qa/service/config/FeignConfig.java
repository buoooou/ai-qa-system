package com.ai.qa.service.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign客户端配置
 * 
 * 配置Feign客户端的超时、重试、日志等策略
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-12
 */
@Configuration
public class FeignConfig {
    
    /**
     * 配置Feign日志级别
     * 
     * @return Logger.Level 日志级别
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // 记录请求方法、URL、响应状态码和执行时间
    }
    
    /**
     * 配置请求超时时间
     * 
     * @return Request.Options 请求选项
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            5000,  // 连接超时时间：5秒
            10000  // 读取超时时间：10秒
        );
    }
    
    /**
     * 配置重试策略
     * 
     * @return Retryer 重试器
     */
    @Bean
    public Retryer retryer() {
        // 最大重试次数：3次，重试间隔：1秒，最大重试间隔：3秒
        return new Retryer.Default(1000, 3000, 3);
    }
}
