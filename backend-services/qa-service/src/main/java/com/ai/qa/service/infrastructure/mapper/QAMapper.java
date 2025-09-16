// com.ai.qa.service.application.mapper.QAMapper.java
package com.ai.qa.service.infrastructure.mapper;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QAMapper {

    SaveHistoryCommand toCommand(SaveHistoryRequest request);

    // DTO转换时使用已有字段映射
    QAHistoryDTO toDto(QAHistory history);

    // PO转换时指定createTime映射到PO的createAt
    @Mapping(source = "createTime", target = "createTime")
    QAHistoryPO toPo(QAHistory history);

    // 从PO转换到领域对象时，使用构造函数注入createTime
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "sessionId", source = "sessionId")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "answer", source = "answer")
    @Mapping(target = "createTime", source = "createTime")
    QAHistory toDomain(QAHistoryPO po);

    @Mapping(target = "createTime", expression = "java(java.time.LocalDateTime.now())")
    QAHistory toDomain(SaveHistoryCommand command);
}