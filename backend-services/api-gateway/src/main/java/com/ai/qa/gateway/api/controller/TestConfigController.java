package com.ai.qa.gateway.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "测试配置接口", description = "用于测试配置的接口")
@RestController
@RequestMapping("/api/test")
public class TestConfigController {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/config")
    @Operation(summary = "测试配置", description = "测试配置是否正确加载")
    public String login(){
        System.out.println("测试config");
        return "测试JWT:" + jwtSecret;
    }
}
