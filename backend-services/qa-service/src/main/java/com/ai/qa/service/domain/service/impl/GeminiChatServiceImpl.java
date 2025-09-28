package com.ai.qa.service.domain.service.impl;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.domain.service.GeminiChatService;
import com.ai.qa.service.domain.service.StreamingChatResult;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Gemini integration service responsible for calling Gemini API to generate chat responses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiChatServiceImpl implements GeminiChatService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent}")
    private String geminiStreamUrl;

    @Value("${gemini.api.generate-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String geminiGenerateUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Calls Gemini API with the provided chat command and returns the response summary.
     *
     * @param command chat completion command containing user prompt
     * @return chat result with answer, token consumption, and latency
     */
    @Override
    public GeminiChatService.ChatResult generateAnswer(ChatCompletionCommand command) {
        try {
            long start = System.currentTimeMillis();
            GeminiChatResponse response = sendGenerateRequest(command)
                    .blockOptional(Duration.ofSeconds(30))
                    .orElseThrow(() -> new IllegalStateException("Gemini returned empty response"));

            if (response.candidates == null || response.candidates.isEmpty()) {
                return new GeminiChatService.ChatResult("", 0, 0, (int) (System.currentTimeMillis() - start));
            }

            GeminiChatResponse.Candidate candidate = response.candidates.get(0);
            String answer = candidate.firstText();
            GeminiChatResponse.UsageMetadata usage = response.usageMetadata;

            int promptTokens = usage != null ? usage.inputTokens : 0;
            int completionTokens = usage != null ? usage.outputTokens : 0;
            int latency = (int) (System.currentTimeMillis() - start);

            return new GeminiChatService.ChatResult(answer, promptTokens, completionTokens, latency);
        } catch (WebClientResponseException ex) {
            log.error("Gemini generate call failed: {}", ex.getResponseBodyAsString(), ex);
            throw new IllegalStateException("Gemini API call failed", ex);
        }
    }

    @Override
    public StreamingChatResult streamAnswer(ChatCompletionCommand command) {
        long start = System.currentTimeMillis();

        Flux<GeminiStreamChunk> chunkFlux = sendStreamingRequest(command)
                .publishOn(Schedulers.boundedElastic())
                .onErrorMap(ex -> new IllegalStateException("Gemini streaming error", ex));

        List<String> accumulatedContent = new ArrayList<>();
        AtomicInteger promptTokens = new AtomicInteger();
        AtomicInteger completionTokens = new AtomicInteger();

        Flux<String> formattedStream = chunkFlux.flatMap(chunk -> {
            if (chunk.error != null) {
                return Flux.error(new IllegalStateException(chunk.error.message));
            }

            if (chunk.usageMetadata != null) {
                promptTokens.set(chunk.usageMetadata.inputTokens);
                completionTokens.set(chunk.usageMetadata.outputTokens);
            }

            if (chunk.candidates != null) {
                return Flux.fromIterable(chunk.candidates)
                        .flatMap(candidate -> {
                            List<String> parts = candidate.textParts();
                            accumulatedContent.addAll(parts);
                            return Flux.fromIterable(parts);
                        })
                        .map(text -> toSse("content", text));
            }

            return Flux.empty();
        }).concatWithValues(toSse("end", ""));

        return new StreamingChatResult(
                formattedStream,
                String.join("", accumulatedContent),
                promptTokens.get(),
                completionTokens.get(),
                (int) (System.currentTimeMillis() - start)
        );
    }

    private WebClient.RequestBodySpec baseRequest(WebClient.RequestBodySpec spec) {
        return spec
                .header("x-goog-api-key", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }

    private Mono<GeminiChatResponse> sendGenerateRequest(ChatCompletionCommand command) {
        GeminiChatRequest request = buildRequestPayload(command);

        return baseRequest(webClient.post().uri(geminiGenerateUrl))
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(GeminiChatResponse.class);
    }

    private Flux<GeminiStreamChunk> sendStreamingRequest(ChatCompletionCommand command) {
        GeminiChatRequest request = buildStreamingRequest(command);

        return baseRequest(webClient.post().uri(geminiStreamUrl))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(GeminiStreamRawChunk.class)
                .map(raw -> Objects.requireNonNullElseGet(raw.toChunk(objectMapper), GeminiStreamChunk::empty));
    }

    private GeminiChatRequest buildRequestPayload(ChatCompletionCommand command) {
        return GeminiChatRequest.builder()
                .contents(buildConversation(command))
                .generationConfig(null)
                .safetySettings(GeminiChatRequest.defaultSafetySettings())
                .build();
    }

    private GeminiChatRequest buildStreamingRequest(ChatCompletionCommand command) {
        return buildRequestPayload(command).withStreamConfig();
    }

    private List<GeminiChatRequest.Content> buildConversation(ChatCompletionCommand command) {
        List<GeminiChatRequest.Content> conversation = new ArrayList<>();
        conversation.add(GeminiChatRequest.Content.system("You are a helpful assistant."));

        if (command.getHistory() != null) {
            command.getHistory().forEach(msg -> conversation.add(GeminiChatRequest.Content.of(msg.getRole(), msg.getContent())));
        }

        conversation.add(GeminiChatRequest.Content.of("user", command.getQuestion()));
        return conversation;
    }

    private String toSse(String type, String text) {
        GeminiSsePayload payload = new GeminiSsePayload(type, text);
        try {
            return "data: " + objectMapper.writeValueAsString(payload) + "\n\n";
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize SSE payload", ex);
        }
    }

    // --- Internal DTOs ---

    @Builder
    private record GeminiChatRequest(
            List<Content> contents,
            @JsonInclude(JsonInclude.Include.NON_NULL) GenerationConfig generationConfig,
            List<SafetySetting> safetySettings
    ) {

        GeminiChatRequest withStreamConfig() {
            return GeminiChatRequest.builder()
                    .contents(contents)
                    .generationConfig(GenerationConfig.streamConfig())
                    .safetySettings(safetySettings)
                    .build();
        }

        static List<SafetySetting> defaultSafetySettings() {
            return List.of(new SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"));
        }

        record Content(List<Part> parts) {
            static Content system(String text) {
                return of("system", text);
            }

            static Content of(String role, String text) {
                return new Content(List.of(new Part(role, text)));
            }
        }

        record Part(@JsonProperty("role") String role, String text) {
        }

        record GenerationConfig(@JsonProperty("responseMimeType") String responseMimeType,
                                @JsonProperty("streaming") boolean streaming) {
            static GenerationConfig streamConfig() {
                return new GenerationConfig("application/json", true);
            }
        }

        record SafetySetting(String category, String threshold) {
        }
    }

    private record GeminiChatResponse(List<Candidate> candidates, UsageMetadata usageMetadata) {

        record Candidate(List<Content> content) {
            String firstText() {
                if (content == null) {
                    return "";
                }
                return content.stream()
                        .flatMap(c -> c.parts.stream())
                        .map(part -> part.text)
                        .filter(StringUtils::hasText)
                        .findFirst()
                        .orElse("");
            }
        }

        record Content(List<Part> parts) {
        }

        record Part(String text) {
        }

        record UsageMetadata(@JsonProperty("inputTokenCount") int inputTokens,
                             @JsonProperty("outputTokenCount") int outputTokens) {
        }
    }

    private static class GeminiStreamRawChunk {
        public List<GeminiStreamChunk.CandidateChunk> candidates;
        public GeminiStreamChunk.ErrorChunk error;
        public GeminiStreamChunk.UsageChunk usageMetadata;

        GeminiStreamChunk toChunk(ObjectMapper mapper) {
            if (candidates == null && error == null && usageMetadata == null) {
                return GeminiStreamChunk.empty();
            }
            return new GeminiStreamChunk(candidates, error, usageMetadata);
        }
    }

    private record GeminiStreamChunk(
            List<CandidateChunk> candidates,
            ErrorChunk error,
            UsageChunk usageMetadata
    ) {
        static GeminiStreamChunk empty() {
            return new GeminiStreamChunk(null, null, null);
        }

        record CandidateChunk(List<ChunkContent> content) {
            List<String> textParts() {
                if (content == null) {
                    return List.of();
                }
                return content.stream()
                        .flatMap(c -> c.parts.stream())
                        .map(part -> part.text)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
            }
        }

        record ChunkContent(List<ChunkPart> parts) {
        }

        record ChunkPart(String text) {
        }

        record ErrorChunk(String message) {
        }

        record UsageChunk(@JsonProperty("inputTokenCount") int inputTokens,
                          @JsonProperty("outputTokenCount") int outputTokens) {
        }
    }

    private record GeminiSsePayload(String type, String text) {
    }
}
