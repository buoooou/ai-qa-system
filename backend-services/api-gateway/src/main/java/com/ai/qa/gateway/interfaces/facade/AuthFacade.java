package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.UserServiceClient;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.LoginGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.RegisterGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import com.ai.qa.gateway.interfaces.dto.UserServiceApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserServiceClient userServiceClient;

    public Mono<AuthResponseDTO> login(LoginGatewayRequestDTO request) {
        return Mono.fromCallable(() -> ensureAuthFields(extractData(userServiceClient.login(request))))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<AuthResponseDTO> register(RegisterGatewayRequestDTO request) {
        return Mono.fromCallable(() -> ensureAuthFields(extractData(userServiceClient.register(request))))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<UserProfileGatewayResponse> profile(Long userId) {
        return Mono.fromCallable(() -> extractData(userServiceClient.profile(userId)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<UserProfileGatewayResponse> updateNickname(Long userId, UpdateNicknameGatewayRequest request) {
        return Mono.fromCallable(() -> extractData(userServiceClient.updateNickname(userId, request)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<List<ChatSessionResponseDTO>> sessions(Long userId) {
        return Mono.fromCallable(() -> extractData(userServiceClient.sessions(userId)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<ChatSessionResponseDTO> createSession(Long userId, CreateSessionGatewayRequest request) {
        return Mono.fromCallable(() -> extractData(userServiceClient.createSession(userId, request)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<ChatSessionResponseDTO> getSession(Long userId, Long sessionId) {
        return Mono.fromCallable(() -> extractData(userServiceClient.getSession(userId, sessionId)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<Void> deleteSession(Long userId, Long sessionId) {
        return Mono.fromRunnable(() -> userServiceClient.deleteSession(userId, sessionId))
                .subscribeOn(Schedulers.boundedElastic())
                .then(Mono.<Void>empty());
    }

    public Mono<List<ChatHistoryResponseDTO>> history(Long userId, Long sessionId, Integer limit) {
        return Mono.fromCallable(() -> extractData(userServiceClient.history(userId, sessionId, limit)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    public Mono<Boolean> isSessionOwnedBy(Long sessionId, Long userId) {
        return Mono.fromCallable(() -> Boolean.TRUE.equals(extractData(userServiceClient.isSessionOwnedBy(sessionId, userId))))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(this::mapFeignException);
    }

    private <T> T extractData(UserServiceApiResponseDTO<T> response) {
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response from user service");
        }
        if (!response.isSuccess()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.getMessage());
        }
        return response.getData();
    }

    private AuthResponseDTO ensureAuthFields(AuthResponseDTO response) {
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty auth response from user service");
        }
        if (response.getToken() == null || response.getToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Auth token missing in response from user service");
        }
        if (response.getProfile() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User profile missing in response from user service");
        }
        return response;
    }
    private Throwable mapFeignException(Throwable throwable) {
        if (throwable instanceof feign.FeignException feignException) {
            HttpStatus status = HttpStatus.resolve(feignException.status());
            String message = extractMessage(feignException);
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return new ResponseStatusException(status, message, feignException);
        }
        return throwable;
    }

    private String extractMessage(feign.FeignException feignException) {
        try {
            String content = feignException.contentUTF8();
            if (content == null || content.isBlank()) {
                return feignException.getMessage();
            }
            // Attempt to parse downstream ApiResponse structure
            java.util.Map<?, ?> map = new com.fasterxml.jackson.databind.ObjectMapper().readValue(content, java.util.Map.class);
            Object message = map.get("message");
            if (message != null) {
                return message.toString();
            }
            return content;
        } catch (Exception ex) {
            return feignException.getMessage();
        }
    }
}
