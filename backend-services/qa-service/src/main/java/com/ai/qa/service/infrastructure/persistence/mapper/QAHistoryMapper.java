package com.ai.qa.service.infrastructure.persistence.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

@Mapper(componentModel = "spring")
public interface QAHistoryMapper {
    QAHistoryMapper INSTANCE = Mappers.getMapper(QAHistoryMapper.class);

    QAHistory toDomain(QAHistoryPO qaHistoryPO);
    List<QAHistory> toDomainList(List<QAHistoryPO> qaHistoryPO);
    QAHistoryPO toPO(QAHistory qaHistory);
    QAHistoryDTO toDto(QAHistory qaHistory);
    List<QAHistoryDTO> toDtoList(List<QAHistory> qaHistory);
}
