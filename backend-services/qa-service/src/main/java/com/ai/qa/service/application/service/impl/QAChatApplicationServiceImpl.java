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
import java.util.Optional;

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

    /**
     * 确保用户会话存在：如果已存在则复用，否则自动创建。
     */
    private String ensureSession(ChatCompletionCommand command) {
        String sessionId = Optional.ofNullable(command.getSessionId())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new IllegalArgumentException("Session ID is required and cannot be empty"));

        Long userId = command.getUserId();
        String title = Optional.ofNullable(command.getSessionTitle()).orElse("New Conversation");

        log.info("🧭 QA service: ensureSession(userId={}, sessionId={})", userId, sessionId);

        // 1️⃣ 尝试查询已有会话
        boolean exists = false;
        try {
            var resp = userClient.getSession(userId, sessionId);
            exists = resp != null && Boolean.TRUE.equals(resp.success()) && resp.data() != null;
            if (exists) {
                log.info("✅ QA service: Session {} already exists for userId={}, will reuse", sessionId, userId);
                return sessionId;
            }
        } catch (Exception e) {
            log.debug("ℹ️ QA service: Failed to query session {} for userId={} (might not exist): {}", sessionId, userId, e.getMessage());
        }

        // 2️⃣ 若不存在则创建新会话
        log.info("🆕 QA service: Creating new session for userId={}, sessionId={}, title='{}'", userId, sessionId, title);

        try {
            var createReq = new UserClient.CreateSessionRequest(sessionId, title);
            var createResp = userClient.createSession(userId, createReq);

            if (createResp == null || !Boolean.TRUE.equals(createResp.success()) || createResp.data() == null) {
                log.error("❌ QA service: Failed to create session. Response={}", createResp);
                throw new IllegalStateException("Failed to create session via user-service");
            }

            log.info("✅ QA service: Session created successfully: id={}", createResp.data().id());
            return createResp.data().id();

        } catch (feign.FeignException fe) {
            // 如果返回409冲突，则说明会话其实已经存在（可能并发创建）
            if (fe.status() == 409) {
                log.warn("⚠️ QA service: Session {} already exists (HTTP 409), reusing existing one", sessionId);
                return sessionId;
            }
            throw fe;
        } catch (Exception e) {
            log.error("💥 QA service: Unexpected error when creating session userId={}, sessionId={}", userId, sessionId, e);
            throw e;
        }
    }

}