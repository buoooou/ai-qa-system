package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.UserServiceClient;
import com.ai.qa.gateway.interfaces.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<AuthResponseDTO> login(LoginGatewayRequestDTO request) {
        return userServiceClient.login(request)
                .map(this::extractData)
                .map(this::ensureAuthFields)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<AuthResponseDTO> register(RegisterGatewayRequestDTO request) {
        return userServiceClient.register(request)
                .map(this::extractData)
                .map(this::ensureAuthFields)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<UserProfileGatewayResponse> profile(Long userId) {
        return userServiceClient.profile(userId)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<UserProfileGatewayResponse> updateNickname(Long userId, UpdateNicknameGatewayRequest request) {
        return userServiceClient.updateNickname(userId, request)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<List<ChatSessionResponseDTO>> sessions(Long userId) {
        return userServiceClient.sessions(userId)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<ChatSessionResponseDTO> createSession(Long userId, CreateSessionGatewayRequest request) {
        return userServiceClient.createSession(userId, request)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<ChatSessionResponseDTO> getSession(Long userId, String sessionId) {
        return userServiceClient.getSession(userId, sessionId)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<Void> deleteSession(Long userId, String sessionId) {
        return userServiceClient.deleteSession(userId, sessionId)
                .then()
                .onErrorMap(this::mapFeignException);
    }


    public Mono<List<ChatHistoryResponseDTO>> history(Long userId, String sessionId, Integer limit) {
        return userServiceClient.history(userId, sessionId, limit)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    public Mono<Boolean> isSessionOwnedBy(String sessionId, Long userId) {
        return userServiceClient.isSessionOwnedBy(sessionId, userId)
                .map(this::extractData)
                .map(Boolean.TRUE::equals)
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
            Map<?, ?> map = objectMapper.readValue(content, Map.class);
            Object message = map.get("message");
            return message != null ? message.toString() : content;
        } catch (Exception ex) {
            return feignException.getMessage();
        }
    }
}
