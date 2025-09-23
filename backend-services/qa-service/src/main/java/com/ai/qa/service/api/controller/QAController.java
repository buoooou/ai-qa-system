package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.service.QAService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
@Slf4j
public class QAController {

    private final QAService qaService;
    private final QAHistoryService qaHistoryService;

    @GetMapping("/test")
    public String testFeign() {
        log.info("测试 feign");
        return qaService.processQuestion(1L);
    }

    /**
     * 处理用户问题，使用 Google AI 生成回答
     */
    @PostMapping("/ask")
    public CompletableFuture<ResponseEntity<Map<String, String>>> askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String userId = request.get("userId");
        
        log.info("收到用户问题，用户ID: {}, 问题: {}", userId, question);
        
        if (question == null || question.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                ResponseEntity.badRequest().body(Map.of("error", "问题不能为空"))
            );
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            userId = "1"; // 默认用户ID
        }
        
        final String finalUserId = userId; // 创建 final 变量
        
        return qaService.processQuestionWithAI(question, finalUserId)
            .thenApply(answer -> {
                log.info("AI 回答生成完成，用户ID: {}, 回答长度: {}", finalUserId, answer.length());
                return ResponseEntity.ok(Map.of("answer", answer));
            })
            .exceptionally(throwable -> {
                log.error("处理问题失败", throwable);
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "处理问题失败: " + throwable.getMessage()));
            });
    }

    @PostMapping("/save")
    public ResponseEntity<QAHistoryDTO> saveHistory(@RequestBody SaveHistoryCommand command) {
        QAHistoryDTO dto = qaHistoryService.saveHistory(command);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<QAHistoryDTO>> getUserHistory(@PathVariable String userId) {
        QAHistoryQuery query = new QAHistoryQuery();
        query.setUserId(userId);
        List<QAHistoryDTO> history = qaHistoryService.queryUserHistory(query);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("QA Service is running!");
    }
}
