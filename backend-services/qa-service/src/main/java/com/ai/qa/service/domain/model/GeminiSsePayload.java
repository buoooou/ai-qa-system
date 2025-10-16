package com.ai.qa.service.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 封装 SSE 推送的内容结构，用于 Gemini 响应流
 */
@Getter
@AllArgsConstructor
public class GeminiSsePayload {

    @JsonProperty("type")
    private final String type;

    @JsonProperty("text")
    private final String text;
}