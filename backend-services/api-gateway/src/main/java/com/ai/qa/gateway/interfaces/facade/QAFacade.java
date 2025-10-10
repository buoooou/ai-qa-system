package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.QAServiceClient;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import com.ai.qa.gateway.interfaces.dto.common.ApiResponseDTO;
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

    public Mono<List<ChatHistoryResponseDTO>> history(Long userId, Long sessionId, Integer limit) {
        return qaServiceClient.history(userId, sessionId, limit)
                .map(this::extractData)
                .onErrorMap(this::mapFeignException);
    }

    private <T> T extractData(ApiResponseDTO<T> response) {
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response from QA service");
        }
        if (!response.isSuccess()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.getMessage());
        }
        return response.getData();
    }

    private Throwable mapFeignException(Throwable throwable) {
        if (throwable instanceof feign.FeignException feignException) {
            HttpStatus status = HttpStatus.resolve(feignException.status());
            String message = feignException.getMessage();
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return new ResponseStatusException(status, message, feignException);
        }
        return throwable;
    }
}
