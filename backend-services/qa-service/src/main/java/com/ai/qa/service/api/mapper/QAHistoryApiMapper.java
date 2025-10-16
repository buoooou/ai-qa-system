package com.ai.qa.service.api.mapper;

import com.ai.qa.service.api.dto.QAHistoryResponse;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QAHistoryApiMapper {

    QAHistoryResponse toResponse(QAHistoryDTO dto);
}
