package com.ai.qa.service.client;

import com.ai.qa.service.dto.ApiResponse;
import com.ai.qa.service.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务Feign客户端
 *
 * 通过Feign声明式HTTP客户端调用user-service的接口
 * 实现服务间通信，获取用户信息
 *
 * @author David
 * @version 1.0
 * @since 2025-09-12
 */
/*@FeignClient(
        name = "user-service-qiaozhe",  // 服务名称，与Nacos中注册的服务名一致
        path = "/api/user"              // 服务路径前缀
)*/
public interface UserServiceClient {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息响应
     */
   /* @GetMapping("/{userId}")
    ApiResponse<UserInfoDto> getUserById(@PathVariable("userId") Long userId);

    *//**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息响应
     *//*
    @GetMapping("/username/{username}")
    ApiResponse<UserInfoDto> getUserByUsername(@PathVariable("username") String username);

    *//**
     * 检查用户服务健康状态
     *
     * @return 健康检查响应
     *//*
    @GetMapping("/health")
    ApiResponse<String> checkHealth();*/
}