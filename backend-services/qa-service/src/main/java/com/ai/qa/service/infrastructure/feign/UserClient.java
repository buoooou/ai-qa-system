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
// 直接访问 user-service，需要允许无认证访问
@FeignClient(name = "user-service-fyb", url = "http://localhost:8081", configuration = FeignConfig.class)
public interface UserClient {

    @PostMapping("/user/{userId}/sessions")
    ApiResponse<ChatSessionPayload> createSession(@PathVariable("userId") Long userId,
                                                  @RequestBody CreateSessionRequest request);

    @GetMapping("/user/{userId}/sessions/{sessionId}")
    ApiResponse<ChatSessionPayload> getSession(@PathVariable("userId") Long userId,
                                               @PathVariable("sessionId") String sessionId);

    @DeleteMapping("/user/{userId}/sessions/{sessionId}")
    ApiResponse<Void> deleteSession(@PathVariable("userId") Long userId,
                                    @PathVariable("sessionId") String sessionId);

    @GetMapping("/user/{userId}/sessions/{sessionId}/history")
    ApiResponse<List<ChatHistoryPayload>> history(@PathVariable("userId") Long userId,
                                                  @PathVariable("sessionId") String sessionId,
                                                  @RequestParam(value = "limit", required = false) Integer limit);

    record CreateSessionRequest(String sessionId, String title) {}

    record ChatSessionPayload(String id, String title, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {}

    record ChatHistoryPayload(Long id, String sessionId, String question, String answer, LocalDateTime createdAt) {}

    record ApiResponse<T>(int code, String message, boolean success, T data) {}
}