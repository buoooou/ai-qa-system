package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.QAServiceClient;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QAFacade {

    private final QAServiceClient qaServiceClient;

    public Flux<String> chat(ChatRequestDTO request) {
        return qaServiceClient.chat(request);
    }

    public List<ChatHistoryResponseDTO> history(Long userId, Long sessionId, Integer limit) {
        return qaServiceClient.history(userId, sessionId, limit).getData();
    }
}
