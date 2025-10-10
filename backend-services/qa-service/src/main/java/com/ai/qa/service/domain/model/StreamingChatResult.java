package com.ai.qa.service.domain.model;

import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class StreamingChatResult {

    /**
     * The stream of Server-Sent Events (SSE) data, ready for the client.
     */
    private final Flux<String> sseStream;

    /**
     * A Mono that, upon completion of the stream, will emit the single, fully concatenated answer.
     * 这是一个未来的结果。
     */
    private final Mono<String> fullAnswerMono;

    // 我们仍然需要原子引用来在流处理期间捕获它们
    private final AtomicInteger promptTokensRef;
    private final AtomicInteger completionTokensRef;
    private final int latencyMs;

    public StreamingChatResult(
            Flux<String> sseStream,
            Mono<String> fullAnswerMono,
            AtomicInteger promptTokensRef,
            AtomicInteger completionTokensRef,
            int latencyMs
    ) {
        this.sseStream = sseStream;
        this.fullAnswerMono = fullAnswerMono;
        this.promptTokensRef = promptTokensRef;
        this.completionTokensRef = completionTokensRef;
        this.latencyMs = latencyMs;
    }

    // 这些方法保持不变，用于在流结束后获取值
    public int promptTokens() {
        return promptTokensRef.get();
    }

    public int completionTokens() {
        return completionTokensRef.get();
    }
}
