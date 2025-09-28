package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.UserServiceClient;
import com.ai.qa.gateway.interfaces.dto.AuthRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserServiceClient userServiceClient;

    public AuthResponseDTO login(AuthRequestDTO request) {
        return userServiceClient.login(request);
    }

    public AuthResponseDTO register(AuthRequestDTO request) {
        return userServiceClient.register(request);
    }

    public UserProfileGatewayResponse profile(Long userId) {
        return userServiceClient.profile(userId).getData();
    }

    public UserProfileGatewayResponse updateNickname(Long userId, UpdateNicknameGatewayRequest request) {
        return userServiceClient.updateNickname(userId, request).getData();
    }

    public List<ChatSessionResponseDTO> sessions(Long userId) {
        return userServiceClient.sessions(userId).getData();
    }

    public ChatSessionResponseDTO createSession(Long userId, CreateSessionGatewayRequest request) {
        return userServiceClient.createSession(userId, request).getData();
    }

    public ChatSessionResponseDTO getSession(Long userId, Long sessionId) {
        return userServiceClient.getSession(userId, sessionId).getData();
    }

    public void deleteSession(Long userId, Long sessionId) {
        userServiceClient.deleteSession(userId, sessionId);
    }

    public List<ChatHistoryResponseDTO> history(Long userId, Long sessionId, Integer limit) {
        return userServiceClient.history(userId, sessionId, limit).getData();
    }

    public boolean isSessionOwnedBy(Long sessionId, Long userId) {
        return Boolean.TRUE.equals(userServiceClient.isSessionOwnedBy(sessionId, userId).getData());
    }

    public List<ChatHistoryResponseDTO> listLatestHistory(Long sessionId, Integer limit) {
        return userServiceClient.listLatestMessages(sessionId, limit).getData();
    }
}
