package com.ai.qa.user.application.mapper;

import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.application.dto.ChatMessageDTO;
import com.ai.qa.user.application.dto.ChatSessionDTO;
import com.ai.qa.user.application.dto.UserProfileDTO;
import com.ai.qa.user.domain.model.QaHistory;
import com.ai.qa.user.domain.model.QaSession;
import com.ai.qa.user.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    default UserProfileDTO toProfile(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name()
        );
    }

    default ChatSessionDTO toSessionDto(QaSession session) {
        return new ChatSessionDTO(
                session.getId(),
                session.getUserId(),
                session.getTitle(),
                session.getStatus().name(),
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    default ChatMessageDTO toMessageDto(QaHistory history) {
        return new ChatMessageDTO(
                history.getId(),
                history.getSessionId(),
                history.getQuestion(),
                history.getAnswer(),
                history.getPromptTokens(),
                history.getCompletionTokens(),
                history.getLatencyMs(),
                history.getCreatedAt(),
                history.getUpdatedAt()
        );
    }
}
