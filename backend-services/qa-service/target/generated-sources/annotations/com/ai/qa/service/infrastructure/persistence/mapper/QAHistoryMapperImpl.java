package com.ai.qa.service.infrastructure.persistence.mapper;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-21T11:47:14+0800",
    comments = "version: 1.5.3.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251001-1143, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class QAHistoryMapperImpl implements QAHistoryMapper {

    @Override
    public QAHistoryPO toPO(QAHistory domain) {
        if ( domain == null ) {
            return null;
        }

        QAHistoryPO qAHistoryPO = new QAHistoryPO();

        qAHistoryPO.setId( domain.getId() );
        qAHistoryPO.setUserId( domain.getUserId() );
        qAHistoryPO.setQuestion( domain.getQuestion() );
        qAHistoryPO.setAnswer( domain.getAnswer() );
        qAHistoryPO.setTimestamp( domain.getTimestamp() );
        qAHistoryPO.setSessionId( domain.getSessionId() );
        qAHistoryPO.setCreateTime( domain.getCreateTime() );
        qAHistoryPO.setUpdateTime( domain.getUpdateTime() );

        return qAHistoryPO;
    }

    @Override
    public QAHistory toDomain(QAHistoryPO po) {
        if ( po == null ) {
            return null;
        }

        QAHistory qAHistory = new QAHistory();

        qAHistory.setId( po.getId() );
        qAHistory.setUserId( po.getUserId() );
        qAHistory.setQuestion( po.getQuestion() );
        qAHistory.setAnswer( po.getAnswer() );
        qAHistory.setTimestamp( po.getTimestamp() );
        qAHistory.setSessionId( po.getSessionId() );
        qAHistory.setCreateTime( po.getCreateTime() );
        qAHistory.setUpdateTime( po.getUpdateTime() );

        return qAHistory;
    }
}
