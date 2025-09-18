package com.ai.qa.service.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QAHistoryMapper {

    QAHistoryPO toPO(QAHistory qaHistory);

    default QAHistory toDomain(QAHistoryPO po) {
        return QAHistory.getInstance(
            po.getUserId(),
            po.getSessionId(),
            po.getQuestion(),
            po.getAnswer(),
            po.getCreateTime()
        );
    }
}
