package com.ai.qa.service.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;


@Mapper(componentModel = "spring")
public interface QAHistoryMapper {

    QAHistoryMapper INSTANCE = Mappers.getMapper(QAHistoryMapper.class);

    QAHistory toDomain(QAHistoryPO historyPO);

    QAHistoryPO toPO(QAHistory history);

}
