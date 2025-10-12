package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.LoginGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.RegisterGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import com.ai.qa.gateway.interfaces.dto.UserServiceApiResponseDTO;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ReactiveFeignClient(name = "user-service-fyb-local", url = "http://localhost:8081")
public interface UserServiceClient {
    @PostMapping("/user/login")
    Mono<UserServiceApiResponseDTO<AuthResponseDTO>> login(@RequestBody LoginGatewayRequestDTO request);

    @PostMapping("/user/register")
    Mono<UserServiceApiResponseDTO<AuthResponseDTO>> register(@RequestBody RegisterGatewayRequestDTO request);

    @GetMapping("/user/{userId}/profile")
    Mono<UserServiceApiResponseDTO<UserProfileGatewayResponse>> profile(@PathVariable("userId") Long userId);

    @PostMapping("/user/{userId}/nickname")
    Mono<UserServiceApiResponseDTO<UserProfileGatewayResponse>> updateNickname(@PathVariable("userId") Long userId,
                                                                         @RequestBody UpdateNicknameGatewayRequest request);

    @GetMapping("/user/{userId}/sessions")
    Mono<UserServiceApiResponseDTO<List<ChatSessionResponseDTO>>> sessions(@PathVariable("userId") Long userId);

    @PostMapping("/user/{userId}/sessions")
    Mono<UserServiceApiResponseDTO<ChatSessionResponseDTO>> createSession(@PathVariable("userId") Long userId,
                                                                    @RequestBody CreateSessionGatewayRequest request);

    @GetMapping("/user/{userId}/sessions/{sessionId}")
    Mono<UserServiceApiResponseDTO<ChatSessionResponseDTO>> getSession(@PathVariable("userId") Long userId,
                                                                 @PathVariable("sessionId") String sessionId);

    @DeleteMapping("/user/{userId}/sessions/{sessionId}")
    Mono<UserServiceApiResponseDTO<Void>> deleteSession(@PathVariable("userId") Long userId,
                                                  @PathVariable("sessionId") String sessionId);

    @GetMapping("/user/{userId}/sessions/{sessionId}/history")
    Mono<UserServiceApiResponseDTO<List<ChatHistoryResponseDTO>>> history(@PathVariable("userId") Long userId,
                                                                    @PathVariable("sessionId") String sessionId,
                                                                    @RequestParam(value = "limit", required = false) Integer limit);

    @GetMapping("/user/sessions/{sessionId}/ownership")
    Mono<UserServiceApiResponseDTO<Boolean>> isSessionOwnedBy(@PathVariable("sessionId") String sessionId,
                                                        @RequestParam("userId") Long userId);
}
