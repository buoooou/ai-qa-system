package com.ai.qa.service.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.infrastructure.persistence.entities.QASessionPO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QASessionMapper {
    QASessionPO toPO(QAHistorySession qaHistorySession);

    default QAHistorySession toDomain(QASessionPO po) {
        return QAHistorySession.getInstance(po.getUserId(), po.getSessionId(), po.getTopic());
    }
}
