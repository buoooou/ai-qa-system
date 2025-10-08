package com.ai.qa.service.infrastructure.persistence.mapper;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QARAG;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.Named;
// import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface QAHistoryMapper {

    // 将 PO 转为领域模型
    default QAHistory toDomain(QAHistoryPO po) {
        if (po == null)
            return null;

        QARAG rag = po.getRagAnswer() == null || po.getRagAnswer().isEmpty()
                ? null
                : new QARAG("", po.getRagAnswer());

        return QAHistory.builder()
                .id(po.getId())
                .userId(po.getUserId())
                .sessionId(po.getSessionId())
                .question(po.getQuestion())
                .answer(po.getAnswer())
                .timestamp(po.getTimestamp())
                .rag(rag)
                .build();
    }

    // 将领域模型转为 PO
    default QAHistoryPO toPO(QAHistory history) {
        if (history == null)
            return null;

        QAHistoryPO po = new QAHistoryPO();
        po.setId(history.getId());
        po.setUserId(history.getUserId());
        po.setSessionId(history.getSessionId());
        po.setQuestion(history.getQuestion());
        po.setAnswer(history.getAnswer());
        po.setTimestamp(history.getTimestamp());
        po.setRagAnswer(history.getRAGAnswer()); // 直接调用 getter
        return po;
    }

    // QAHistoryMapper INSTANCE = Mappers.getMapper(QAHistoryMapper.class);

    // @Mapping(target = "rag", expression = "java(toRAG(po.getRagAnswer()))")
    // QAHistory toDomain(QAHistoryPO po);

    // @Mapping(target = "ragAnswer", source = "rag", qualifiedByName = "fromRAG")
    // QAHistoryPO toPO(QAHistory history);

    // default QARAG toRAG(String ragAnswer) {
    // if (ragAnswer == null || ragAnswer.isEmpty())
    // return null;
    // return new QARAG("", ragAnswer);
    // }

    // @Named("fromRAG")
    // default String fromRAG(QARAG rag) {
    // if (rag == null)
    // return null;
    // return rag.getGeneratedAnswer();
    // }
}
