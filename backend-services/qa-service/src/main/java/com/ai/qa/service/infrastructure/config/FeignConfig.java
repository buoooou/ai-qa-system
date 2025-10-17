package com.ai.qa.service.infrastructure.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;

/**
 * Feign客户端配置类 用于配置Feign客户端的全局行为
 */
@Configuration
public class FeignConfig {

    /**
     * 配置Feign客户端的日志级别 FULL级别会记录请求和响应的头信息、正文和元数据
     *
     * @return Logger.Level.FULL 最详细的日志级别
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 配置请求超时时间
     *
     * @return Request.Options 请求选项
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5, TimeUnit.SECONDS,
                3, TimeUnit.SECONDS,
                true);
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
