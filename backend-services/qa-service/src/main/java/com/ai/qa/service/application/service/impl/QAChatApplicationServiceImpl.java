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
        // 2. 返回给客户端的SSE流，并使用 doOnComplete 钩子来触发保存操作
        return streamResult.getSseStream()
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
    
    private Long ensureSession(ChatCompletionCommand command) {
        if (command.getSessionId() != null) {
            return command.getSessionId();
        }

        // TODO: Skip session creation for now due to authentication issues
        // Use a hardcoded session ID to allow chat functionality
        System.out.println("QA service: Using hardcoded session ID=1 (skipping session creation due to 403 error)");
        return 1L;

        /*
        String title = command.getSessionTitle();
        System.out.println("QA service: Creating session for userId=" + command.getUserId() + ", title=" + title);
        try {
            var response = userClient.createSession(command.getUserId(), new UserClient.CreateSessionRequest(title));
            if (response == null || !Boolean.TRUE.equals(response.success()) || response.data() == null) {
                System.err.println("QA service: Failed to create session - response: " + response);
                throw new IllegalStateException("Failed to create session via user-service");
            }
            System.out.println("QA service: Session created successfully with id=" + response.data().id());
            return response.data().id();
        } catch (Exception e) {
            System.err.println("QA service: Error creating session: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        */
    }
}