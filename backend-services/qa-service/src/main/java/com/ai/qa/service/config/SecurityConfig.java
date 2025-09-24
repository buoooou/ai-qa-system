package com.ai.qa.service.config;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //定义拦截对象
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/login", // 前端静态资源
                        "/",
                        "/*.html",
                        "/*/*.html",
                        "/*.js",
                        "/*.css",
                        "/static/**",
                        "/assets/**",
                        "/images/**",
                        "/favicon.ico",
                        // Swagger-UI
                        "/swagger-ui/**",
                        "/v3/api-docs/**",   // 如果也用了 springdoc-openapi
                        "/swagger-resources/**",
                        "/api-docs/swagger-config",
                        "/api-docs");
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 所有路径
                .allowedOrigins("*")   // 允许所有来源
                .allowedMethods("*")   // 允许所有HTTP方法
                .allowedHeaders("*");  // 允许所有请求头
        // 注意: 使用通配符(*)时不能设置allowCredentials(true)
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")          // 允许任何请求头
                        .exposedHeaders("*")          // 让前端也能读到响应头
                        .allowCredentials(true);      // 允许带 Authorization/Cookie
            }
        };
    }


}