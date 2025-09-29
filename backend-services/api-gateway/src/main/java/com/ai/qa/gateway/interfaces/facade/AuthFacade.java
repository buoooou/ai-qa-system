package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.UserServiceClient;
import com.ai.qa.gateway.interfaces.dto.AuthRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.RegisterGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserServiceClient userServiceClient;

    public Mono<AuthResponseDTO> login(AuthRequestDTO request) {
        return Mono.fromCallable(() -> userServiceClient.login(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<AuthResponseDTO> register(RegisterGatewayRequestDTO request) {
        return Mono.fromCallable(() -> userServiceClient.register(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserProfileGatewayResponse> profile(Long userId) {
        return Mono.fromCallable(() -> userServiceClient.profile(userId).getData())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserProfileGatewayResponse> updateNickname(Long userId, UpdateNicknameGatewayRequest request) {
        return Mono.fromCallable(() -> userServiceClient.updateNickname(userId, request).getData())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<ChatSessionResponseDTO>> sessions(Long userId) {
        return Mono.fromCallable(() -> userServiceClient.sessions(userId).getData())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ChatSessionResponseDTO> createSession(Long userId, CreateSessionGatewayRequest request) {
        return Mono.fromCallable(() -> userServiceClient.createSession(userId, request).getData())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<ChatSessionResponseDTO> getSession(Long userId, Long sessionId) {
        return Mono.fromCallable(() -> userServiceClient.getSession(userId, sessionId).getData())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteSession(Long userId, Long sessionId) {
        return Mono.fromRunnable(() -> userServiceClient.deleteSession(userId, sessionId))
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.<Void>empty());
    }

    public Mono<List<ChatHistoryResponseDTO>> history(Long userId, Long sessionId, Integer limit) {
        return Mono.fromCallable(() -> userServiceClient.history(userId, sessionId, limit).getData())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> isSessionOwnedBy(Long sessionId, Long userId) {
        return Mono.fromCallable(() -> Boolean.TRUE.equals(userServiceClient.isSessionOwnedBy(sessionId, userId).getData()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
