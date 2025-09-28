package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import com.ai.qa.gateway.interfaces.dto.QAHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.facade.QAFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@Tag(name = "Gateway QA", description = "QA proxy endpoints exposed by the gateway")
@RestController
@RequestMapping("/api/gateway/qa")
@RequiredArgsConstructor
@Validated
public class QAGatewayController {

    private final QAFacade qaFacade;

    @Operation(summary = "Proxy chat", description = "Delegates chat requests to qa-service-fyb.")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> chat(@RequestBody @Validated ChatRequestDTO request) {
        Flux<String> body = qaFacade.chat(request);
        return ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(body);
    }

    @Operation(summary = "Proxy history", description = "Retrieves chat history from qa-service-fyb.")
    @GetMapping("/history")
    public ResponseEntity<List<QAHistoryResponseDTO>> history(@Parameter(description = "User ID") @RequestParam Long userId,
                                                              @Parameter(description = "Session ID") @RequestParam(required = false) Long sessionId,
                                                              @Parameter(description = "Maximum items") @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(qaFacade.history(userId, sessionId, limit));
    }
}
