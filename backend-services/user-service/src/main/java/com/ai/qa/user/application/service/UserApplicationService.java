package com.ai.qa.user.application.service;

import com.ai.qa.user.application.dto.ChatMessageDTO;
import com.ai.qa.user.application.dto.ChatSessionDTO;
import com.ai.qa.user.application.dto.UserProfileDTO;
import com.ai.qa.user.domain.model.User;

import java.util.List;

public interface UserApplicationService {

    User updateNickname(Long userId, String nickname);

    UserProfileDTO getProfile(Long userId);

    List<ChatSessionDTO> listSessions(Long userId);

    List<ChatMessageDTO> listHistoryBySession(Long userId, String sessionId);

    ChatSessionDTO createSession(Long userId, String sessionId, String title);

    ChatSessionDTO getSession(Long userId, String sessionId);

    void deleteSession(Long userId, String sessionId);

    boolean isSessionOwnedBy(String sessionId, Long userId);
}