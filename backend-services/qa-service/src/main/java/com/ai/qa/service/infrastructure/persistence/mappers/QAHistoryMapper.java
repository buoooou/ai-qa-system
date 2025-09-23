package com.ai.qa.service.infrastructure.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QAHistoryMapper {

    default QAHistoryPO toPO(QAHistory qaHistory){
        QAHistoryPO po = new QAHistoryPO();
        po.setUserId(qaHistory.getUserId());
        po.setSessionId(qaHistory.getSessionId());
        po.setQuestion(qaHistory.getQuestion());
        po.setAnswer(qaHistory.getAnswer());
        return po;
    }

    default QAHistory toDomain(QAHistoryPO po) {
        if (po == null) {
            return null;
        }
        return QAHistory.getInstance(
            po.getUserId(),
            po.getSessionId(),
            po.getQuestion(),
            po.getAnswer(),
            po.getCreateTime()
        );
    }
}
