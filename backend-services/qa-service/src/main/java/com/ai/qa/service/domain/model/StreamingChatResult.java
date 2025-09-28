package com.ai.qa.service.domain.service;

import reactor.core.publisher.Flux;

public record StreamingChatResult(
        Flux<String> stream,
        String fullAnswer,
        Integer promptTokens,
        Integer completionTokens,
        Integer latencyMs
) {
}
