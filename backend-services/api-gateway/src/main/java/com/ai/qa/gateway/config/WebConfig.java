package com.ai.qa.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { // 建议类名改为WebConfig或CorsConfig，更清晰

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 对所有接口路径生效
                // 1. 使用 allowedOriginPatterns 替代 allowedOrigins
                .allowedOriginPatterns("*")

                // 生产环境推荐的配置 (注释掉上面的 "*" 配置，使用下面的)
                // .allowedOrigins("https://your-frontend-domain.com", "http://localhost:3000")

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 2. 明确指定允许的方法
                .allowedHeaders("*") // 允许所有请求头
                .allowCredentials(true) // 3. 允许携带凭证
                .maxAge(3600); // 预检请求的有效时间
    }
}