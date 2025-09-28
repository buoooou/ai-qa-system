package com.ai.qa.service.application.mapper;

import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T21:10:39+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class QAHistoryMapperImpl implements QAHistoryMapper {

    @Override
    public QAHistoryDTO toDto(QAHistory history) {
        if ( history == null ) {
            return null;
        }

        QAHistoryDTO.QAHistoryDTOBuilder qAHistoryDTO = QAHistoryDTO.builder();

        qAHistoryDTO.id( history.getId() );
        qAHistoryDTO.sessionId( history.getSessionId() );
        qAHistoryDTO.userId( history.getUserId() );
        qAHistoryDTO.question( history.getQuestion() );
        qAHistoryDTO.answer( history.getAnswer() );
        qAHistoryDTO.promptTokens( history.getPromptTokens() );
        qAHistoryDTO.completionTokens( history.getCompletionTokens() );
        qAHistoryDTO.latencyMs( history.getLatencyMs() );
        qAHistoryDTO.createdAt( history.getCreatedAt() );
        qAHistoryDTO.updatedAt( history.getUpdatedAt() );

        return qAHistoryDTO.build();
    }

    @Override
    public QAHistoryPO toEntity(QAHistory history) {
        if ( history == null ) {
            return null;
        }

        QAHistoryPO.QAHistoryPOBuilder qAHistoryPO = QAHistoryPO.builder();

        qAHistoryPO.id( history.getId() );
        qAHistoryPO.sessionId( history.getSessionId() );
        qAHistoryPO.userId( history.getUserId() );
        qAHistoryPO.question( history.getQuestion() );
        qAHistoryPO.answer( history.getAnswer() );
        qAHistoryPO.promptTokens( history.getPromptTokens() );
        qAHistoryPO.completionTokens( history.getCompletionTokens() );
        qAHistoryPO.latencyMs( history.getLatencyMs() );
        qAHistoryPO.createdAt( history.getCreatedAt() );
        qAHistoryPO.updatedAt( history.getUpdatedAt() );

        return qAHistoryPO.build();
    }

    @Override
    public QAHistory toDomain(QAHistoryPO entity) {
        if ( entity == null ) {
            return null;
        }

        QAHistory.QAHistoryBuilder qAHistory = QAHistory.builder();

        qAHistory.id( entity.getId() );
        qAHistory.sessionId( entity.getSessionId() );
        qAHistory.userId( entity.getUserId() );
        qAHistory.question( entity.getQuestion() );
        qAHistory.answer( entity.getAnswer() );
        qAHistory.promptTokens( entity.getPromptTokens() );
        qAHistory.completionTokens( entity.getCompletionTokens() );
        qAHistory.latencyMs( entity.getLatencyMs() );
        qAHistory.createdAt( entity.getCreatedAt() );
        qAHistory.updatedAt( entity.getUpdatedAt() );

        return qAHistory.build();
    }
}
