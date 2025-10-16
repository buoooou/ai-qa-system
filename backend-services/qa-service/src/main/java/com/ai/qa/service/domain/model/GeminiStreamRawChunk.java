package com.ai.qa.service.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public class GeminiStreamRawChunk {

    private JsonNode data;

    public GeminiStreamChunk toChunk(ObjectMapper mapper) {
        try {
            return mapper.treeToValue(data, GeminiStreamChunk.class);
        } catch (Exception e) {
            return GeminiStreamChunk.empty();
        }
    }
}