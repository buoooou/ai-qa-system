package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAService qaService;
    private final QAHistoryService qaHistoryService;

    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        return qaService.processQuestion(1L);
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
