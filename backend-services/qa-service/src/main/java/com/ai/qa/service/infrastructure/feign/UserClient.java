package com.ai.qa.service.infrastructure.feign;

import com.ai.qa.service.infrastructure.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调用 user-service 的 Feign 客户端
 */
// 临时配置：直接访问本地暴露的 user-service 端口
// 原配置: @FeignClient(name = "user-service-fyb")  // 通过 Nacos 服务发现
@FeignClient(name = "user-service-fyb", url = "http://localhost:8081", configuration = FeignConfig.class)  // 直接 URL 访问
public interface UserClient {

    @PostMapping("/api/user/{userId}/sessions")
    ApiResponse<ChatSessionPayload> createSession(@PathVariable("userId") Long userId,
                                                  @RequestBody CreateSessionRequest request);

    @GetMapping("/api/user/{userId}/sessions/{sessionId}")
    ApiResponse<ChatSessionPayload> getSession(@PathVariable("userId") Long userId,
                                               @PathVariable("sessionId") Long sessionId);

    @DeleteMapping("/api/user/{userId}/sessions/{sessionId}")
    ApiResponse<Void> deleteSession(@PathVariable("userId") Long userId,
                                    @PathVariable("sessionId") Long sessionId);

    @GetMapping("/api/user/{userId}/sessions/{sessionId}/history")
    ApiResponse<List<ChatHistoryPayload>> history(@PathVariable("userId") Long userId,
                                                  @PathVariable("sessionId") Long sessionId,
                                                  @RequestParam(value = "limit", required = false) Integer limit);

    record CreateSessionRequest(String title) {}

    record ChatSessionPayload(Long id, String title, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {}

    record ChatHistoryPayload(Long id, Long sessionId, String question, String answer, LocalDateTime createdAt) {}

    record ApiResponse<T>(int code, String message, boolean success, T data) {}
}