package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.QAHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.common.ApiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "qa-service-fyb", contextId = "gatewayQaClient")
public interface QAServiceClient {

    @GetMapping("/api/qa/history")
    ApiResponseDTO<List<QAHistoryResponseDTO>> history(@RequestParam("userId") Long userId,
                                                       @RequestParam(value = "sessionId", required = false) Long sessionId,
                                                       @RequestParam(value = "limit", required = false) Integer limit);
}
