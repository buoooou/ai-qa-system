package com.ai.qa.gateway.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController // 控制器注解
@RequestMapping("/api/test") // 控制器路径
@RequiredArgsConstructor // Lombok注解，自动生成构造函数，并注入UserServiceClient依赖
public class TestConfigController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/config")
    public String login() {
        System.out.println("测试config");
        return "测试JWT：" + jwtSecret;
    }

}
