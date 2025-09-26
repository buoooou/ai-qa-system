package com.ai.qa.service.application.service.impl;

import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.mapper.QAHistoryMapper;
import com.ai.qa.service.application.service.QAHistoryApplicationService;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service handling QA history persistence and query operations.
 */
@Service
@RequiredArgsConstructor
public class QAHistoryApplicationServiceImpl implements QAHistoryApplicationService {

    private final QAHistoryRepo historyRepo;
    private final QAHistoryMapper mapper;

    /**
     * Saves a history record based on the provided command.
     *
     * @param command history save command
     * @return saved history DTO
     */
    @Override
    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        QAHistory history = QAHistory.create(command.getSessionId(), command.getUserId(), command.getQuestion());
        history.updateQuestion(command.getQuestion(), command.getPromptTokens());
        history.recordAnswer(command.getAnswer(), command.getCompletionTokens(), command.getLatencyMs());
        QAHistory saved = historyRepo.save(history);
        return mapper.toDto(saved);
    }

    /**
     * Queries history by user (and optional session) with optional limit.
     *
     * @param query parameter bundle for history search
     * @return list of history DTOs
     */
    @Override
    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query) {
        return historyRepo.findByUserAndSession(query.getUserId(), query.getSessionId(), query.getLimit())
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
