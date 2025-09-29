package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import com.ai.qa.gateway.interfaces.dto.common.ApiResponseDTO;
import com.ai.qa.gateway.interfaces.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
    public Mono<ApiResponseDTO<UserProfileGatewayResponse>> profile(@PathVariable Long userId,
                                                                    ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.profile(userId)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway update nickname", description = "Delegates nickname update to user-service-fyb.")
    @PostMapping("/{userId}/nickname")
    public Mono<ApiResponseDTO<UserProfileGatewayResponse>> updateNickname(@PathVariable Long userId,
                                                                           @RequestBody @Validated UpdateNicknameGatewayRequest request,
                                                                           ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.updateNickname(userId, request)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway chat sessions", description = "Delegates chat session listing to user-service-fyb.")
    @GetMapping("/{userId}/sessions")
    public Mono<ApiResponseDTO<List<ChatSessionResponseDTO>>> sessions(@PathVariable Long userId,
                                                                       ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.sessions(userId)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway create session", description = "Delegates session creation to user-service-fyb.")
    @PostMapping("/{userId}/sessions")
    public Mono<ApiResponseDTO<ChatSessionResponseDTO>> createSession(@PathVariable Long userId,
                                                                      @RequestBody @Validated CreateSessionGatewayRequest request,
                                                                      ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.createSession(userId, request)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway get session", description = "Retrieves a specific chat session from user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}")
    public Mono<ApiResponseDTO<ChatSessionResponseDTO>> getSession(@PathVariable Long userId,
                                                                   @PathVariable Long sessionId,
                                                                   ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.getSession(userId, sessionId)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway delete session", description = "Deletes a chat session using user-service-fyb.")
    @DeleteMapping("/{userId}/sessions/{sessionId}")
    public Mono<ApiResponseDTO<Void>> deleteSession(@PathVariable Long userId,
                                                    @PathVariable Long sessionId,
                                                    ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.deleteSession(userId, sessionId)
                            .thenReturn(ApiResponseDTO.success(null));
                });
    }

    @Operation(summary = "Gateway chat history", description = "Delegates chat history retrieval to user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}/history")
    public Mono<ApiResponseDTO<List<ChatHistoryResponseDTO>>> history(@PathVariable Long userId,
                                                                      @PathVariable Long sessionId,
                                                                      @RequestParam(required = false) Integer limit,
                                                                      ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.history(userId, sessionId, limit)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway check session ownership", description = "Checks if a user owns a specific chat session via user-service-fyb.")
    @GetMapping("/sessions/{sessionId}/ownership")
    public Mono<ApiResponseDTO<Boolean>> isSessionOwnedBy(@PathVariable Long sessionId,
                                                          @RequestParam Long userId,
                                                          ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.isSessionOwnedBy(sessionId, userId)
                            .map(ApiResponseDTO::success);
                });
    }

    @Operation(summary = "Gateway list latest chat history", description = "Retrieves the latest chat messages for a given session via user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}/history/latest")
    public Mono<ApiResponseDTO<List<ChatHistoryResponseDTO>>> listLatestHistory(@PathVariable Long userId,
                                                                                @PathVariable Long sessionId,
                                                                                @RequestParam(required = false) Integer limit,
                                                                                ServerWebExchange exchange) {
        return resolveUserId(exchange)
                .flatMap(tokenUserId -> {
                    if (!tokenUserId.equals(userId)) {
                        return Mono.just(ApiResponseDTO.failure(HttpStatus.FORBIDDEN.value(), "Access denied"));
                    }
                    return authFacade.history(userId, sessionId, limit)
                            .map(ApiResponseDTO::success);
                });
    }

    private Mono<Long> resolveUserId(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (header == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user identity"));
        }
        try {
            return Mono.just(Long.parseLong(header));
        } catch (NumberFormatException ex) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user identity"));
        }
    }
}
