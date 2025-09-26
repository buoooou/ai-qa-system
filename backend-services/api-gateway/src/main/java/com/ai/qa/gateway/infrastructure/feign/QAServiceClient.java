package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import com.ai.qa.gateway.interfaces.dto.QAHistoryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "qa-service-fyb", contextId = "gatewayQaClient")
public interface QAServiceClient {

    @PostMapping("/api/qa/chat")
    QAHistoryResponseDTO chat(@RequestBody ChatRequestDTO request);

    @GetMapping("/api/qa/history")
    List<QAHistoryResponseDTO> history(@RequestParam("userId") Long userId,
                                       @RequestParam(value = "sessionId", required = false) Long sessionId,
                                       @RequestParam(value = "limit", required = false) Integer limit);
}
