package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.QAServiceClient;
import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QAFacade {

    private final QAServiceClient qaServiceClient;

    public Flux<String> chat(ChatRequestDTO request) {
        return qaServiceClient.chat(request);
    }
}
