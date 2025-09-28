package com.ai.qa.service.domain.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeminiStreamChunk {

    private final List<Candidate> candidates;
    private final UsageMetadata usageMetadata;
    private final Error error;

    public static GeminiStreamChunk empty() {
        return GeminiStreamChunk.builder().build();
    }

    @Getter
    @Builder
    public static class Candidate {
        private final List<Part> parts;

        public List<String> textParts() {
            return parts.stream().map(Part::getText).toList();
        }
    }

    @Getter
    @Builder
    public static class Part {
        private final String text;
    }

    @Getter
    @Builder
    public static class UsageMetadata {
        private final int inputTokens;
        private final int outputTokens;
    }

    @Getter
    @Builder
    public static class Error {
        private final String message;
    }
}