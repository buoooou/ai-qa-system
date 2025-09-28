package com.ai.qa.service.infrastructure.feign;

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
// name/value 属性值必须与目标服务在 Nacos 上注册的服务名完全一致！
@FeignClient(name = "user-service-fyb")
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