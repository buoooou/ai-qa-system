package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.QAServiceClient;
import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import com.ai.qa.gateway.interfaces.dto.QAHistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QAFacade {

    private final WebClient.Builder webClientBuilder;
    private final QAServiceClient qaServiceClient;

    @Value("${services.qa.base-url:http://qa-service-fyb}")
    private String qaServiceBaseUrl;

    public Map<String, Object> chat(ChatRequestDTO request) {
        WebClient client = webClientBuilder.baseUrl(qaServiceBaseUrl).build();
        var response = client.post()
                .uri("/api/qa/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) {
            throw new IllegalStateException("QA service returned empty response");
        }

        return response;
    }

    public List<QAHistoryResponseDTO> history(Long userId, Long sessionId, Integer limit) {
        return qaServiceClient.history(userId, sessionId, limit).getData();
    }
}
