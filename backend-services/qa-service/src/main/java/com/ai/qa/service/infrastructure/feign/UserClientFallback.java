package com.ai.qa.service.infrastructure.feign;

import com.ai.qa.service.api.dto.ApiResponse;
import com.ai.qa.service.api.dto.UserInfoDTO;
import com.ai.qa.service.api.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

/**
 * UserClient的降级处理类
 * 当user-service服务不可用时，自动执行此类中的方法返回降级结果
 * 避免服务雪崩，提高系统容错性
 */
// @Component
@Slf4j
public class UserClientFallback implements UserClient {

    /**
     * 获取用户信息的降级处理
     */
    @Override
    public ApiResponse<UserInfoDTO> getUserById(Long userId) {
        log.warn("UserService unavailable, fallback triggered for userId: {}", userId);
        return ApiResponse.error(ErrorCode.SERVICE_UNAVAILABLE);
    }

    /**
     * 获取用户信息的降级处理
     */
    @Override
    public ApiResponse<UserInfoDTO> getUserByUsername(String username) {
        log.warn("UserService unavailable, fallback triggered for username: {}", username);
        // return "{\"error\": \"用户服务暂时不可用\", \"userId\": " + username + "}";
        return ApiResponse.error(ErrorCode.SERVICE_UNAVAILABLE);
    }

    /**
     * 获取用户状态的降级处理
     */
    @Override
    public String getUserStatus(Long userId) {
        log.warn("UserService unavailable, fallback triggered for user status: {}", userId);
        return "{\"status\": \"unknown\", \"userId\": " + userId + "}";
    }

    /**
     * 获取用户基本信息的降级处理
     */
    @Override
    public ApiResponse<UserInfoDTO> getUserBasicInfo(Long userId) {
        log.warn("UserService unavailable, fallback triggered for user basic info: {}", userId);
        // return "{\"name\": \"未知用户\", \"userId\": " + userId + "}";
        return ApiResponse.error(ErrorCode.SERVICE_UNAVAILABLE);
    }

    @Override
    public ApiResponse<String> checkHealth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}