package com.ai.qa.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Nacos配置测试类
 * 用于验证共享配置是否正确加载
 */
@Slf4j
@Configuration
@RefreshScope
@RestController
public class NacosConfigTest {
    
    @Value("${jwt.secret:未配置}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:0}")
    private Long jwtExpiration;
    
    // 硬编码测试配置
    private final String testSecret = "a-very-long-and-secure-secret-key-for-ai-qa-system-ssss-xxxx";
    private final Long testExpiration = 86400000L;
    
    /**
     * 测试JWT配置是否加载成功
     */
    @GetMapping("/test/jwt-config")
    public String testJwtConfig() {
        log.info("JWT Secret: {}", jwtSecret);
        log.info("JWT Expiration: {}", jwtExpiration);
        
        return String.format("JWT配置测试:\nSecret: %s\nExpiration: %d ms", 
                           jwtSecret, jwtExpiration);
    }
    
    /**
     * 测试Nacos连接状态
     */
    @GetMapping("/test/nacos-status")
    public String testNacosStatus() {
        log.info("Nacos配置测试接口被调用");
        return "Nacos配置管理测试成功！";
    }
    
    /**
     * 测试所有环境变量和配置
     */
    @GetMapping("/test/config-debug")
    public String testConfigDebug() {
        log.info("=== 配置调试信息 ===");
        log.info("JWT Secret: {}", jwtSecret);
        log.info("JWT Expiration: {}", jwtExpiration);
        
        return String.format("配置调试信息:\n" +
                           "JWT Secret: %s\n" +
                           "JWT Expiration: %d\n" +
                           "Secret长度: %d\n" +
                           "Expiration类型: %s\n\n" +
                           "硬编码测试配置:\n" +
                           "Test Secret: %s\n" +
                           "Test Expiration: %d", 
                           jwtSecret, jwtExpiration, 
                           jwtSecret != null ? jwtSecret.length() : 0,
                           jwtExpiration != null ? jwtExpiration.getClass().getSimpleName() : "null",
                           testSecret, testExpiration);
    }
}
