package com.ai.qa.service.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record GeminiChatResponse(
        List<Candidate> candidates,
        UsageMetadata usageMetadata
) {

    public record Candidate(List<Content> content) {
        public String firstText() {
            if (content == null) return "";
            return content.stream()
                    .flatMap(c -> c.parts.stream())
                    .map(Part::text)
                    .filter(t -> t != null && !t.isBlank())
                    .findFirst()
                    .orElse("");
        }
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record UsageMetadata(
            @JsonProperty("inputTokenCount") int inputTokens,
            @JsonProperty("outputTokenCount") int outputTokens
    ) {
    }
}