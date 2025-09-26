package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.ChatCompletionRequest;
import com.ai.qa.service.api.dto.QAHistoryResponse;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.api.mapper.QAHistoryApiMapper;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.service.QAChatApplicationService;
import com.ai.qa.service.application.service.QAHistoryApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QA APIs", description = "Gemini chat proxy and history management")
@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
@Validated
public class QAController {

    private final QAChatApplicationService chatApplicationService;
    private final QAHistoryApplicationService historyApplicationService;
    private final QAHistoryApiMapper apiMapper;

    @Operation(summary = "Gemini chat", description = "Handles a chat request by proxying to Gemini and persisting history.")
    @PostMapping("/chat")
    public ResponseEntity<QAHistoryResponse> chat(@RequestBody @Validated ChatCompletionRequest request) {
        var result = chatApplicationService.chat(request.toCommand());
        return ResponseEntity.ok(apiMapper.toResponse(result));
    }

    @Operation(summary = "Save history", description = "Persists a chat message and returns the saved entity.")
    @PostMapping("/history")
    public ResponseEntity<QAHistoryResponse> saveHistory(@RequestBody @Validated SaveHistoryRequest request) {
        var dto = historyApplicationService.saveHistory(request.toCommand());
        return ResponseEntity.ok(apiMapper.toResponse(dto));
    }

    @Operation(summary = "Query history", description = "Fetches chat history filtered by user and optional session.")
    @GetMapping("/history")
    public ResponseEntity<List<QAHistoryResponse>> queryHistory(@Parameter(description = "History query parameters") @Validated QAHistoryQuery query) {
        var list = historyApplicationService.queryUserHistory(query).stream()
                .map(apiMapper::toResponse)
                .toList();
        return ResponseEntity.ok(list);
    }
}
