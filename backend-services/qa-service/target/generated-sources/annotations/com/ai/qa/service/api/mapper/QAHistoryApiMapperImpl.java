package com.ai.qa.service.api.mapper;

import com.ai.qa.service.api.dto.QAHistoryResponse;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T21:10:39+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class QAHistoryApiMapperImpl implements QAHistoryApiMapper {

    @Override
    public QAHistoryResponse toResponse(QAHistoryDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Long id = null;
        Long sessionId = null;
        Long userId = null;
        String question = null;
        String answer = null;
        Integer promptTokens = null;
        Integer completionTokens = null;
        Integer latencyMs = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = dto.getId();
        sessionId = dto.getSessionId();
        userId = dto.getUserId();
        question = dto.getQuestion();
        answer = dto.getAnswer();
        promptTokens = dto.getPromptTokens();
        completionTokens = dto.getCompletionTokens();
        latencyMs = dto.getLatencyMs();
        createdAt = dto.getCreatedAt();
        updatedAt = dto.getUpdatedAt();

        QAHistoryResponse qAHistoryResponse = new QAHistoryResponse( id, sessionId, userId, question, answer, promptTokens, completionTokens, latencyMs, createdAt, updatedAt );

        return qAHistoryResponse;
    }
}
