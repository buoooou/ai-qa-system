package com.ai.qa.service.application.service.impl;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.mapper.QAHistoryMapper;
import com.ai.qa.service.application.service.QAChatApplicationService;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.domain.service.GeminiChatService;
import com.ai.qa.service.infrastructure.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Application service orchestrating Gemini chat requests and history persistence.
 */
@Service
@RequiredArgsConstructor
public class QAChatApplicationServiceImpl implements QAChatApplicationService {

    private final QAHistoryRepo historyRepo;
    private final GeminiChatService geminiChatService;
    private final QAHistoryMapper mapper;
    private final UserClient userClient;

    /**
     * Handles a chat completion workflow: calls Gemini, persists the result, and returns the saved record.
     *
     * @param command chat completion command containing user/session/question details
     * @return saved history DTO representing the conversation turn
     */
    @Override
    public QAHistoryDTO chat(ChatCompletionCommand command) {
        Long sessionId = ensureSession(command);
        var result = geminiChatService.generateAnswer(command);
        QAHistory history = QAHistory.create(sessionId, command.getUserId(), command.getQuestion());
        history.updateQuestion(command.getQuestion(), result.promptTokens());
        history.recordAnswer(result.answer(), result.completionTokens(), result.latencyMs());
        QAHistory saved = historyRepo.save(history);
        return mapper.toDto(saved);
    }

    @Override
    public Flux<String> chatStream(ChatCompletionCommand command) {
        Long sessionId = ensureSession(command);
        QAHistory history = QAHistory.create(sessionId, command.getUserId(), command.getQuestion());
        history.updateQuestion(command.getQuestion(), null);

        var streamResult = geminiChatService.streamAnswer(command.withSessionId(sessionId));

        return streamResult.stream()
                .doOnComplete(() -> {
                    history.recordAnswer(streamResult.fullAnswer(), streamResult.completionTokens(), streamResult.latencyMs());
                    historyRepo.save(history);
                });
    }

    private Long ensureSession(ChatCompletionCommand command) {
        if (command.getSessionId() != null) {
            return command.getSessionId();
        }

        String title = command.getSessionTitle();
        var response = userClient.createSession(command.getUserId(), new UserClient.CreateSessionRequest(title));
        if (response == null || !response.success() || response.data() == null) {
            throw new IllegalStateException("Failed to create session via user-service");
        }
        return response.data().id();
    }
}
