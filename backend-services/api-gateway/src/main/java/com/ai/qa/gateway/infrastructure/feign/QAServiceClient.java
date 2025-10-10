package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import com.ai.qa.gateway.interfaces.dto.UserServiceApiResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.common.ApiResponseDTO;

import reactivefeign.spring.config.ReactiveFeignClient;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "qa-service-fyb-local", url = "http://localhost:8082")
public interface QAServiceClient {

    @PostMapping(value = "/api/qa/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> chat(@RequestBody ChatRequestDTO request);

    @GetMapping("/api/qa/history")
    Mono<ApiResponseDTO<List<ChatHistoryResponseDTO>>> history(@RequestParam("userId") Long userId,
                                                       @RequestParam(value = "sessionId", required = false) Long sessionId,
                                                       @RequestParam(value = "limit", required = false) Integer limit);
}
