package com.ai.qa.service.application.mapper;

import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper bridging domain aggregates and DTO/entities.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QAHistoryMapperImpl {

    QAHistoryDTO toDto(QAHistory history);

    QAHistoryPO toEntity(QAHistory history);

    QAHistory toDomain(QAHistoryPO entity);
}
