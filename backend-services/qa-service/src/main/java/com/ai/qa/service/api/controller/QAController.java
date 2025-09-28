package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.ChatCompletionRequest;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.service.QAChatApplicationService;
import com.ai.qa.service.application.service.QAHistoryApplicationService;
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

@Tag(name = "QA APIs", description = "Gemini chat proxy and history management")
@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
@Validated
public class QAController {

    private final QAChatApplicationService chatApplicationService;
    private final QAHistoryApplicationService historyApplicationService;

    @Operation(summary = "Gemini chat", description = "Handles a chat request by proxying to Gemini and persisting history.")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody @Validated ChatCompletionRequest request) {
        return chatApplicationService.chatStream(request.toCommand());
    }

    @Operation(summary = "Save history", description = "Persists a chat message and returns the saved entity.")
    @PostMapping("/history")
    public ResponseEntity<Void> saveHistory(@RequestBody @Validated SaveHistoryRequest request) {
        historyApplicationService.saveHistory(request.toCommand());
        return ResponseEntity.ok().build();
    }

}
