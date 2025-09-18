package com.ai.qa.qaservice.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {

    // 从配置中读取gateway.secretId（需确保微服务A的配置中存在该值，可通过Nacos共享配置）
    @Value("${gateway.secretId}")
    private String gatewaySecretId;

    @Bean
    public RequestInterceptor gatewaySecretInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                log.info("Feign请求拦截器, 添加X-Gateway-Secret头部: {}", gatewaySecretId);
                // 为所有Feign请求添加X-Gateway-Secret头部
                template.header("X-Gateway-Secret", gatewaySecretId);
            }
        };
    }
}