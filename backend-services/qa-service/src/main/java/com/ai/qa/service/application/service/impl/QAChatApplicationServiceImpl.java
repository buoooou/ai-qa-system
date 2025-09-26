package com.ai.qa.service.application.service.impl;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.mapper.QAHistoryMapper;
import com.ai.qa.service.application.service.QAChatApplicationService;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.domain.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application service orchestrating Gemini chat requests and history persistence.
 */
@Service
@RequiredArgsConstructor
public class QAChatApplicationServiceImpl implements QAChatApplicationService {

    private final QAHistoryRepo historyRepo;
    private final GeminiChatService geminiChatService;
    private final QAHistoryMapper mapper;

    /**
     * Handles a chat completion workflow: calls Gemini, persists the result, and returns the saved record.
     *
     * @param command chat completion command containing user/session/question details
     * @return saved history DTO representing the conversation turn
     */
    @Override
    public QAHistoryDTO chat(ChatCompletionCommand command) {
        var result = geminiChatService.generateAnswer(command);
        QAHistory history = QAHistory.create(command.getSessionId(), command.getUserId(), command.getQuestion());
        history.updateQuestion(command.getQuestion(), result.promptTokens());
        history.recordAnswer(result.answer(), result.completionTokens(), result.latencyMs());
        QAHistory saved = historyRepo.save(history);
        return mapper.toDto(saved);
    }
}
