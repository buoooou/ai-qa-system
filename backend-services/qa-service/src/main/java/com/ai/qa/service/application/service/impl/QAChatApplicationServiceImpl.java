package com.ai.qa.service.application.service.impl;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.mapper.QAHistoryMapperImpl;
import com.ai.qa.service.application.service.QAChatApplicationService;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.StreamingChatResult;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.domain.service.GeminiChatService;
import com.ai.qa.service.infrastructure.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class QAChatApplicationServiceImpl implements QAChatApplicationService {

    private final QAHistoryRepo historyRepo;
    private final GeminiChatService geminiChatService;
    private final QAHistoryMapperImpl mapper;
    private final UserClient userClient;

    // 非流式方法保持不变
    @Override
    public QAHistoryDTO chat(ChatCompletionCommand command) {
        String sessionId = ensureSession(command);
        var result = geminiChatService.generateAnswer(command);
        QAHistory history = QAHistory.create(sessionId, command.getUserId(), command.getQuestion());
        history.updateQuestion(command.getQuestion(), result.promptTokens());
        history.recordAnswer(result.answer(), result.completionTokens(), result.latencyMs());
        QAHistory saved = historyRepo.save(history);
        return mapper.toDto(saved);
    }

    @Override
    public Flux<String> chatStream(ChatCompletionCommand command) {
        String sessionId = ensureSession(command);
        QAHistory history = QAHistory.create(sessionId, command.getUserId(), command.getQuestion());
        history.updateQuestion(command.getQuestion(), null);

        StreamingChatResult streamResult = geminiChatService.streamAnswer(command.withSessionId(sessionId));

        // 1. 定义一个在流结束后执行的、用于保存历史的 Mono<Void>
        Mono<Void> saveOperation = streamResult.getFullAnswerMono()
                .flatMap(fullAnswer ->
                        // 将阻塞操作包装在 Mono.fromRunnable 中
                        Mono.fromRunnable(() -> {
                            log.info("Stream completed for session {}. Full answer length: {}. Saving to DB...", sessionId, fullAnswer.length());
                            history.recordAnswer(
                                    fullAnswer,
                                    streamResult.completionTokens(),
                                    streamResult.getLatencyMs()
                            );
                            historyRepo.save(history);
                            log.info("✅ History successfully saved for session {}", sessionId);
                        })
                )
                // 在专门用于阻塞IO的线程池上执行
                .subscribeOn(Schedulers.boundedElastic())
                .then(); // 转换成 Mono<Void>

        // ✅✅✅ --- 最终修复 --- ✅✅✅
        // 2. 首先发送会话 ID，然后发送实际的 SSE 流
        Flux<String> sessionIdStream = Flux.just(String.format("data: {\"type\":\"session-id\",\"sessionId\":\"%s\"}\\n\\n", sessionId));

        return Flux.concat(sessionIdStream, streamResult.getSseStream())
                .doOnComplete(() -> {
                    // 当 sseStream 完成后，"订阅" (即触发) 我们定义好的 saveOperation。
                    // 这是一个 fire-and-forget 操作，不会影响返回给客户端的流。
                    saveOperation.subscribe(
                        null, // onNext - Mono<Void> 没有 next 信号
                        error -> log.error(
                            "❌ Asynchronous history save failed for session {}",
                            sessionId,
                            error
                        ) // onError - 记录后台保存任务的任何错误
                    );
                });
    }

    private String ensureSession(ChatCompletionCommand command) {
        // sessionId现在必须提供
        if (command.getSessionId() == null || command.getSessionId().trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID is required and cannot be empty");
        }

        String sessionId = command.getSessionId();
        log.info("QA service: Checking if session exists for userId={}, sessionId={}", command.getUserId(), sessionId);

        // 先检查session是否存在
        try {
            var sessionResponse = userClient.getSession(command.getUserId(), sessionId);
            if (sessionResponse != null && Boolean.TRUE.equals(sessionResponse.success()) && sessionResponse.data() != null) {
                log.info("QA service: Session {} already exists, reusing it", sessionId);
                return sessionId; // session已存在，直接使用
            }
        } catch (Exception e) {
            log.debug("QA service: Session {} not found, will create new one", sessionId);
            // session不存在，继续创建逻辑
        }

        // session不存在，创建新的session
        String title = command.getSessionTitle();
        log.info("QA service: Creating new session for userId={}, sessionId={}, title={}", command.getUserId(), sessionId, title);

        try {
            var response = userClient.createSession(command.getUserId(), new UserClient.CreateSessionRequest(sessionId, title));
            if (response == null || !Boolean.TRUE.equals(response.success()) || response.data() == null) {
                log.error("QA service: Failed to create session - response: {}", response);
                throw new IllegalStateException("Failed to create session via user-service");
            }
            log.info("QA service: New session created successfully with id={}", response.data().id());
            return response.data().id();
        } catch (Exception e) {
            log.error("QA service: Error creating session", e);
            throw e;
        }
    }
}