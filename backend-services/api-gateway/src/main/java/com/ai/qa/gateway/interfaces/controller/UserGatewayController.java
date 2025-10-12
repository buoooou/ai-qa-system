package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.interfaces.dto.*;
import com.ai.qa.gateway.interfaces.dto.common.ApiResponseDTO;
import com.ai.qa.gateway.interfaces.facade.AuthFacade;
import com.ai.qa.gateway.infrastructure.config.TokenWebFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.function.Supplier;

import java.util.List;

@Tag(name = "Gateway User", description = "User service proxy endpoints exposed by the gateway")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/gateway/user")
@RequiredArgsConstructor
@Validated
public class UserGatewayController {

    private final AuthFacade authFacade;

    @Operation(summary = "Gateway user profile", description = "Delegates profile retrieval to user-service-fyb.")
    @GetMapping("/{userId}/profile")
    public Mono<ApiResponseDTO<UserProfileGatewayResponse>> profile(@PathVariable Long userId) {
        return withAuth(userId, () -> authFacade.profile(userId).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway update nickname", description = "Delegates nickname update to user-service-fyb.")
    @PostMapping("/{userId}/nickname")
    public Mono<ApiResponseDTO<UserProfileGatewayResponse>> updateNickname(@PathVariable Long userId,
                                                                           @RequestBody @Validated UpdateNicknameGatewayRequest request) {
        return withAuth(userId, () -> authFacade.updateNickname(userId, request).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway chat sessions", description = "Delegates chat session listing to user-service-fyb.")
    @GetMapping("/{userId}/sessions")
    public Mono<ApiResponseDTO<List<ChatSessionResponseDTO>>> sessions(@PathVariable Long userId) {
        return withAuth(userId, () -> authFacade.sessions(userId).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway create session", description = "Delegates session creation to user-service-fyb. Can optionally specify a session ID.")
    @PostMapping("/{userId}/sessions")
    public Mono<ApiResponseDTO<ChatSessionResponseDTO>> createSession(@PathVariable Long userId,
                                                                      @RequestBody @Validated CreateSessionGatewayRequest request) {
        return withAuth(userId, () -> authFacade.createSession(userId, request).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway get session", description = "Retrieves a specific chat session from user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}")
    public Mono<ApiResponseDTO<ChatSessionResponseDTO>> getSession(@PathVariable Long userId,
                                                                   @PathVariable String sessionId) {
        return withAuth(userId, () -> authFacade.getSession(userId, sessionId).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway delete session", description = "Deletes a chat session using user-service-fyb.")
    @DeleteMapping("/{userId}/sessions/{sessionId}")
    public Mono<ApiResponseDTO<Void>> deleteSession(@PathVariable Long userId,
                                                    @PathVariable String sessionId) {
        return withAuth(userId, () -> authFacade.deleteSession(userId, sessionId)
                .thenReturn(ApiResponseDTO.success(null)));
    }

    @Operation(summary = "Gateway chat history", description = "Delegates chat history retrieval to user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}/history")
    public Mono<ApiResponseDTO<List<ChatHistoryResponseDTO>>> history(@PathVariable Long userId,
                                                                      @PathVariable String sessionId,
                                                                      @RequestParam(required = false) Integer limit) {
        return withAuth(userId, () -> authFacade.history(userId, sessionId, limit).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway check session ownership", description = "Checks if a user owns a specific chat session via user-service-fyb.")
    @GetMapping("/sessions/{sessionId}/ownership")
    public Mono<ApiResponseDTO<Boolean>> isSessionOwnedBy(@PathVariable String sessionId,
                                                          @RequestParam Long userId) {
        return withAuth(userId, () -> authFacade.isSessionOwnedBy(sessionId, userId).map(ApiResponseDTO::success));
    }

    @Operation(summary = "Gateway list latest chat history", description = "Retrieves the latest chat messages for a given session via user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}/history/latest")
    public Mono<ApiResponseDTO<List<ChatHistoryResponseDTO>>> listLatestHistory(@PathVariable Long userId,
                                                                                @PathVariable String sessionId,
                                                                                @RequestParam(required = false) Integer limit) {
        return withAuth(userId, () -> authFacade.history(userId, sessionId, limit).map(ApiResponseDTO::success));
    }

    /**
     * 通用授权上下文读取和校验逻辑
     */
    private <T> Mono<ApiResponseDTO<T>> withAuth(Long userId, Supplier<Mono<ApiResponseDTO<T>>> action) {
        return Mono.deferContextual(ctx -> {
            String token = ctx.getOrDefault(TokenWebFilter.TOKEN_KEY, null);
            Long currentUserId = ctx.getOrDefault(TokenWebFilter.USER_ID_KEY, null);

            if (token == null || currentUserId == null) {
                return Mono.just(ApiResponseDTO.failure(HttpStatus.UNAUTHORIZED.value(), "Missing token"));
            }
            if (!currentUserId.equals(userId)) {
                return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
            }

            return action.get();
        });
    }
}