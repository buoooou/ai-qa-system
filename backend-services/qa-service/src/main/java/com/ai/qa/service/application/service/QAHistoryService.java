// com.ai.qa.service.application.service.QAHistoryService.java
package com.ai.qa.service.application.service;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.infrastructure.mapper.QAMapper;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

public interface QAHistoryService {


    public QAHistoryDTO saveHistory(SaveHistoryCommand command);

    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query);
}