package com.ai.qa.service.domain.service.impl;

import com.ai.qa.service.application.dto.ChatCompletionCommand;
import com.ai.qa.service.domain.model.GeminiSsePayload;
import com.ai.qa.service.domain.model.StreamingChatResult;
import com.ai.qa.service.domain.service.GeminiChatService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiChatServiceImpl implements GeminiChatService {

  private final WebClient geminiWebClient;
  private final ObjectMapper objectMapper;

  @Value("${gemini.api.key}")
  private String apiKey;
  @Value("${gemini.api.generate-url}")
  private String geminiGenerateUrl;
  @Value("${gemini.api.stream-url}")
  private String geminiStreamUrl;

  // ... 非流式方法 generateAnswer 保持不变 ...
  @Override
  public ChatResult generateAnswer(ChatCompletionCommand command) {
    // ... Code from previous correct version ...
    try {
      long start = System.currentTimeMillis();
      GeminiChatRequest request = buildRequestPayload(command, false);
      log.info("📤 Gemini sync request: {}", toJson(request));
      GeminiChatResponse response = baseRequest(geminiWebClient.post().uri(geminiGenerateUrl))
          .body(BodyInserters.fromValue(request))
          .retrieve()
          .bodyToMono(GeminiChatResponse.class)
          .blockOptional(Duration.ofSeconds(30))
          .orElseThrow(() -> new IllegalStateException("Gemini returned empty response"));
      if (response.candidates == null || response.candidates.isEmpty()) {
        return new ChatResult("", 0, 0, (int) (System.currentTimeMillis() - start));
      }
      String answer = response.candidates.get(0).firstText();
      int promptTokens = Optional.ofNullable(response.usageMetadata).map(u -> u.promptTokens).orElse(0);
      int completionTokens = Optional.ofNullable(response.usageMetadata).map(u -> u.candidatesTokens).orElse(0);
      return new ChatResult(answer, promptTokens, completionTokens, (int) (System.currentTimeMillis() - start));
    } catch (WebClientResponseException ex) {
      log.error("❌ Gemini API error: {}", ex.getResponseBodyAsString(), ex);
      throw new IllegalStateException("Gemini API call failed", ex);
    }
  }

  @Override
  public StreamingChatResult streamAnswer(ChatCompletionCommand command) {
    long start = System.currentTimeMillis();
    GeminiChatRequest request = buildRequestPayload(command, true);
    log.info("📤 Gemini stream request being sent to URI: {}", geminiStreamUrl);

    AtomicInteger promptTokens = new AtomicInteger(0);
    AtomicInteger completionTokens = new AtomicInteger(0);

    // ======================= 最终修复 =======================
    Flux<String> rawBodyFlux = baseRequest(geminiWebClient.post().uri(geminiStreamUrl))
        .accept(MediaType.APPLICATION_JSON) // Gemini stream a invalid SSE, so we accept JSON
        .body(BodyInserters.fromValue(request))
        .retrieve()
        .onStatus(status -> !status.is2xxSuccessful(), response -> response.bodyToMono(String.class)
            .flatMap(body -> {
              log.error("❌ Gemini Stream API returned non-2xx status [{}]: {}", response.statusCode(), body);
              return Mono.error(new IllegalStateException("Gemini stream failed with status " + response.statusCode()));
            }))
        .bodyToFlux(String.class);

    // 将所有碎片拼接成一个完整的、有效的 JSON 数组字符串
    Mono<String> fullJsonMono = rawBodyFlux
        .collect(Collectors.joining())
        .map(rawJson -> {
          // Gemini 的流式响应以 [ 开始，以 ] 结束，中间用 , 分隔
          // 有时可能不是标准的SSE，而是一个JSON数组的流
          if (!rawJson.trim().startsWith("[")) {
            return "[" + rawJson + "]";
          }
          return rawJson;
        });

    // 从修复后的完整JSON中解析出所有的数据块
    Flux<StreamPart> sourceStream = fullJsonMono
        .flatMapMany(fullJson -> {
          try {
            log.trace("Attempting to parse full JSON array: {}", fullJson);
            List<GeminiStreamChunk> chunks = objectMapper.readValue(fullJson, new TypeReference<>() {
            });
            log.info("Successfully parsed {} chunks from the stream.", chunks.size());
            return Flux.fromIterable(chunks)
                .flatMap(this::extractPartsFromChunk);
          } catch (Exception e) {
            log.error("CRITICAL: Failed to parse the complete JSON stream. Raw JSON: {}", fullJson, e);
            return Flux.error(e);
          }
        });
    // =========================================================

    Flux<StreamPart> cachedSource = sourceStream.cache();

    Flux<String> textPartsFlux = cachedSource
        .doOnNext(part -> {
          if (part instanceof TokenUsagePart) {
            TokenUsagePart tokenPart = (TokenUsagePart) part;
            promptTokens.set(tokenPart.promptTokens);
            completionTokens.set(tokenPart.completionTokens);
          }
        })
        .ofType(TextPart.class)
        .map(TextPart::text);

    Flux<String> sseStream = textPartsFlux
        .map(text -> toSse("content", text))
        .concatWith(Mono.just(toSse("end", "")));

    Mono<String> fullAnswerMono = textPartsFlux
        .collect(Collectors.joining());

    return new StreamingChatResult(
        sseStream,
        fullAnswerMono,
        promptTokens,
        completionTokens,
        (int) (System.currentTimeMillis() - start));
  }

  // 从一个有效的 chunk 对象中提取 StreamPart
  private Flux<StreamPart> extractPartsFromChunk(GeminiStreamChunk chunk) {
    List<StreamPart> parts = new ArrayList<>();
    if (chunk.usageMetadata() != null) {
      parts.add(new TokenUsagePart(
          chunk.usageMetadata().promptTokens(),
          chunk.usageMetadata().candidatesTokens()));
    }
    if (chunk.candidates() != null) {
      chunk.candidates().stream()
          .flatMap(c -> c.textParts().stream())
          .map(TextPart::new)
          .forEach(parts::add);
    }
    return Flux.fromIterable(parts);
  }

  // ... 内部 record 和辅助方法保持不变 ...
  private sealed interface StreamPart permits TextPart, TokenUsagePart {
  }

  private record TextPart(String text) implements StreamPart {
  }

  private record TokenUsagePart(int promptTokens, int completionTokens) implements StreamPart {
  }

  private WebClient.RequestBodySpec baseRequest(WebClient.RequestBodySpec spec) {
    return spec.header("x-goog-api-key", apiKey).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
  }

  private GeminiChatRequest buildRequestPayload(ChatCompletionCommand command, boolean streaming) {
    List<GeminiChatRequest.Content> contents = new ArrayList<>();
    if (streaming) {
      contents.add(GeminiChatRequest.Content.of("user", command.getQuestion()));
    } else {
      contents.add(GeminiChatRequest.Content.of("system", "You are a helpful assistant."));
      if (command.getHistory() != null) {
        command.getHistory().forEach(msg -> contents
            .add(GeminiChatRequest.Content.of(msg.getRole() == null ? "user" : msg.getRole(), msg.getContent())));
      }
      contents.add(GeminiChatRequest.Content.of("user", command.getQuestion()));
    }
    return new GeminiChatRequest(contents, new GeminiChatRequest.GenerationConfig("application/json"),
        GeminiChatRequest.defaultSafetySettings());
  }

  private String toSse(String type, String text) {
    try {
      GeminiSsePayload payload = new GeminiSsePayload(type, text);
      return "data: " + objectMapper.writeValueAsString(payload) + "\n\n";
    } catch (Exception e) {
      throw new IllegalStateException("SSE serialization failed", e);
    }
  }

  private String toJson(Object obj) {
    try {
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (Exception e) {
      return "{}";
    }
  }

  private record GeminiChatRequest(List<Content> contents,
      @JsonInclude(JsonInclude.Include.NON_NULL) GenerationConfig generationConfig,
      List<SafetySetting> safetySettings) {
    static List<SafetySetting> defaultSafetySettings() {
      return List.of(new SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"));
    }

    record Content(String role, List<Part> parts) {
      static Content of(String role, String text) {
        return new Content(role, List.of(new Part(text)));
      }
    }

    record Part(String text) {
    }

    record GenerationConfig(@JsonProperty("responseMimeType") String responseMimeType) {
    }

    record SafetySetting(String category, String threshold) {
    }
  }

  private record GeminiChatResponse(List<Candidate> candidates, UsageMetadata usageMetadata) {
    record Candidate(Content content) {
      String firstText() {
        if (content == null || content.parts == null)
          return "";
        return content.parts.stream().map(Part::text).filter(StringUtils::hasText).findFirst().orElse("");
      }
    }

    record Content(List<Part> parts) {
    }

    record Part(String text) {
    }

    record UsageMetadata(@JsonProperty("promptTokenCount") int promptTokens,
        @JsonProperty("candidatesTokenCount") int candidatesTokens) {
    }
  }

  private record GeminiStreamChunk(List<Candidate> candidates, ErrorMessage error, UsageMetadata usageMetadata) {
    record Candidate(Content content) {
      List<String> textParts() {
        if (content == null || content.parts() == null) {
          return Collections.emptyList();
        }
        return content.parts().stream().map(Part::text).filter(StringUtils::hasText).collect(Collectors.toList());
      }
    }

    record Content(List<Part> parts, String role) {
    }

    record Part(String text) {
    }

    record ErrorMessage(int code, String message, String status) {
    }

    record UsageMetadata(@JsonProperty("promptTokenCount") int promptTokens,
        @JsonProperty("candidatesTokenCount") int candidatesTokens) {
    }
  }
}