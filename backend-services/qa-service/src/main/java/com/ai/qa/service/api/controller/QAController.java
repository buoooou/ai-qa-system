package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.AskQuestionRequest;
import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.api.dto.QAResponseDTO;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.service.QAService;
import com.ai.qa.service.infrastructure.feign.GeminiClient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;
    private final QAHistoryService qaHistoryService;
    private final GeminiClient geminiClient;

    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        return qaService.processQuestion(1L, "测试问题").getAnswer();
    }

    /**
     * 保存一条 QA 历史记录，并自动调用 Gemini AI 获取答案
     */
    @PostMapping("/save")
    public ResponseEntity<QAHistoryDTO> saveHistory(@Valid @RequestBody SaveHistoryRequest request) {

        // 1. 构建 SaveHistoryCommand
        SaveHistoryCommand command = new SaveHistoryCommand(
                request.getUserId(),
                request.getQuestion(),
                request.getAnswer(),
                null,
                request.getSessionId());

        QAHistoryDTO dto = qaHistoryService.saveHistory(command);

        return ResponseEntity.ok(dto);
    }

    /**
     * 提问接口 - 返回结构化回答
     */
    @PostMapping("/ask")
    public ResponseEntity<QAResponseDTO> askQuestion(
            @RequestBody AskQuestionRequest request) {
        System.out.println("Received: userId=" + request.getUserId() + ", question=" + request.getQuestion());
        Long userId = request.getUserId();
        String question = request.getQuestion();

        if (userId == null || question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    new QAResponseDTO(userId, null, question,
                            "用户ID或问题不能为空", null));
        }
        // 调用 QAService 自动处理 user 信息 + Gemini AI
        QAResponseDTO response = qaService.processQuestion(userId, question);
        return ResponseEntity.ok(response);
    }
}