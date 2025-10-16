package com.ai.qa.user.application.service.impl;

import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrorCode;
import com.ai.qa.user.application.dto.ChatMessageDTO;
import com.ai.qa.user.application.dto.ChatSessionDTO;
import com.ai.qa.user.application.dto.UserProfileDTO;
import com.ai.qa.user.application.mapper.UserMapper;
import com.ai.qa.user.application.service.UserApplicationService;
import com.ai.qa.user.domain.model.QaSession;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repositories.QaHistoryRepository;
import com.ai.qa.user.domain.repositories.QaSessionRepository;
import com.ai.qa.user.domain.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ai.qa.user.common.CommonUtil;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

    private final UserRepository userRepository;
    private final QaSessionRepository sessionRepository;
    private final QaHistoryRepository historyRepository;
    private final UserMapper userMapper;

    /**
     * Updates the nickname for the specified user.
     *
     * @param userId   target user id
     * @param nickname new nickname value
     * @return updated {@link User}
     */
    @Override
    @Transactional
    public User updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.changeNickname(nickname);
        return userRepository.save(user);
    }

    /**
     * Fetches profile information for the specified user.
     *
     * @param userId user id
     * @return profile DTO
     */
    @Override
    public UserProfileDTO getProfile(Long userId) {
        CommonUtil.assertOwner(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toProfile(user);
    }

    /**
     * Lists chat sessions for a user ordered by creation time.
     *
     * @param userId user id
     * @return list of chat session DTOs
     */
    @Override
    public List<ChatSessionDTO> listSessions(Long userId) {
        CommonUtil.assertOwner(userId);
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(userMapper::toSessionDto)
                .toList();
    }

    @Override
    @Transactional
    public ChatSessionDTO createSession(Long userId, String sessionId, String title) {
        CommonUtil.assertOwner(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 验证sessionId参数
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Session ID is required and cannot be empty");
        }

        String resolvedTitle = Optional.ofNullable(title)
                .filter(current -> !current.isBlank())
                .orElse("New Conversation");

        // 检查sessionId是否已存在
        if (sessionRepository.findById(sessionId).isPresent()) {
            throw new BusinessException(ErrorCode.SESSION_ALREADY_EXISTS, "Session ID already exists");
        }

        // 创建session
        QaSession session = sessionRepository.save(QaSession.create(sessionId, user.getId(), resolvedTitle));
        return userMapper.toSessionDto(session);
    }

    @Override
    public ChatSessionDTO getSession(Long userId, String sessionId) {
        CommonUtil.assertOwner(userId);
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该会话");
        }
        return userMapper.toSessionDto(session);
    }

    @Override
    public boolean isSessionOwnedBy(String sessionId, Long userId) {
        CommonUtil.assertOwner(userId);
        return sessionRepository.findById(sessionId)
                .map(session -> session.getUserId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional
    public void deleteSession(Long userId, String sessionId) {
        CommonUtil.assertOwner(userId);
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除该会话");
        }

        // 先删除相关的聊天历史记录
        historyRepository.deleteBySessionId(sessionId);

        // 再删除session
        sessionRepository.delete(session);
    }

    /**
     * Fetches chat history for a given session, verifying the user owns the session.
     *
     * @param userId    user id
     * @param sessionId session id
     * @return chronological list of chat messages
     */
    @Override
    public List<ChatMessageDTO> listHistoryBySession(Long userId, String sessionId) {
        CommonUtil.assertOwner(userId);
        QaSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));
        if (!session.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该会话");
        }
        return historyRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(userMapper::toMessageDto)
                .toList();
    }
}
