package com.ai.qa.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ai.qa.service.api.dto.ApiResponse;
import com.ai.qa.service.api.dto.UserInfoDTO;


/**
 * 用户服务Feign客户端接口
 * 用于调用user-service微服务的REST API
 *
 * @FeignClient 注解说明：
 *              - name: 指定要调用的服务在注册中心的服务名称
 *              - fallback: 指定服务降级处理类，当服务不可用时执行降级逻辑
 */
// name/value 属性值必须与目标服务在 Nacos 上注册的服务名完全一致！
@FeignClient(name = "user-service")
public interface UserClient {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息的JSON字符串
     *
     * 注意：
     * 1. @GetMapping 里的路径必须与 user-service 中 Controller 方法的完整路径匹配。
     * 2. 方法签名 (方法名、参数) 可以自定义，但 @PathVariable, @RequestParam 等注解必须和远程接口保持一致。
     */
    @GetMapping("/api/user/{userId}")
    ApiResponse<UserInfoDTO> getUserById(@PathVariable("userId") Long userId);

    /**
     * 根据用户名获取用户完整信息
     *
     * @param username 用户名
     * @return 用户信息的JSON字符串
     */
    @GetMapping("/api/user/username/{username}")
    ApiResponse<UserInfoDTO> getUserByUsername(@PathVariable("username") String username);

    /**
     * 获取用户状态信息
     *
     * @param userId 用户ID
     * @return 用户状态信息的JSON字符串
     */
    @GetMapping("/api/user/{userId}/status")
    String getUserStatus(@PathVariable("userId") Long userId);

    /**
     * 获取用户基本信息
     *
     * @param userId 用户ID
     * @return 用户基本信息的JSON字符串
     */
    @GetMapping("/api/user/{userId}/basic-info")
    ApiResponse<UserInfoDTO> getUserBasicInfo(@PathVariable("userId") Long userId);

    /**
     * 检查用户服务健康状态
     * 
     * @return 健康检查响应
     */
    @GetMapping("/health")
    ApiResponse<String> checkHealth();
}