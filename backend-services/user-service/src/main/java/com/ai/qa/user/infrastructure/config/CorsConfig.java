package com.ai.qa.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 配置跨域信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有来源（生产环境需指定具体域名）
        config.setAllowCredentials(true); // 允许携带Cookie
        config.addAllowedMethod("*"); // 允许所有HTTP方法（GET/POST等）
        config.addAllowedHeader("*"); // 允许所有请求头

        // 2. 配置路径映射
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对所有接口生效

        return new CorsFilter(source);
    }
}