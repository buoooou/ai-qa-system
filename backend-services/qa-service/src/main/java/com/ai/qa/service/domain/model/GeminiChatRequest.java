package com.ai.qa.service.domain.model;

import com.ai.qa.service.domain.model.GeminiChatRequest.GenerationConfig;
import com.ai.qa.service.domain.model.GeminiChatRequest.SafetySetting;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeminiChatRequest(
        List<Content> contents,
        GenerationConfig generationConfig,
        List<SafetySetting> safetySettings
) {

    public GeminiChatRequest withStreamConfig() {
        return new GeminiChatRequest(contents, GenerationConfig.streamConfig(), safetySettings);
    }

    public static List<SafetySetting> defaultSafetySettings() {
        return List.of(new SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"));
    }

    public record Content(List<Part> parts) {
        public static Content system(String text) {
            return of("system", text);
        }

        public static Content of(String role, String text) {
            return new Content(List.of(new Part(role, text)));
        }
    }

    public record Part(@JsonProperty("role") String role, String text) {
    }

    public record GenerationConfig(@JsonProperty("responseMimeType") String responseMimeType,
                                   @JsonProperty("streaming") boolean streaming) {
        public static GenerationConfig streamConfig() {
            return new GenerationConfig("application/json", true);
        }
    }

    public record SafetySetting(String category, String threshold) {
    }
}