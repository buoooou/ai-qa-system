// com.ai.qa.service.api.controller.QAController.java
package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.infrastructure.mapper.QAMapper;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.ai.qa.service.application.service.QAHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/qa")
@Tag(name = "QA管理", description = "QA历史记录接口")
@RequiredArgsConstructor
public class QAController {

    private final QAHistoryService qaHistoryService;
    private final QAMapper qaMapper;

    @PostMapping("/save")
    public ResponseEntity<QAHistoryDTO> saveHistory(@Valid @RequestBody SaveHistoryRequest request) {
        // 使用mapstruct转换
        return ResponseEntity.ok(
                qaHistoryService.saveHistory(qaMapper.toCommand(request))
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<QAHistoryDTO>> queryHistory(
            @RequestParam Long userId,
            @RequestParam(required = false) String sessionId) {
        QAHistoryQuery query = new QAHistoryQuery();
        query.setUserId(userId);
        query.setSessionId(sessionId);

        return ResponseEntity.ok(qaHistoryService.queryUserHistory(query));
    }
}