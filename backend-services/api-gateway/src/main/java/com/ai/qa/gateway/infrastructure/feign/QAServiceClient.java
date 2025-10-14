package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import reactivefeign.spring.config.ReactiveFeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;

// @ReactiveFeignClient(name = "qa-service-fyb-local", url = "http://localhost:8082")
@ReactiveFeignClient(name = "qa-service-fyb")
public interface QAServiceClient {

    @PostMapping(value = "/api/qa/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> chat(@RequestBody ChatRequestDTO request);
}
