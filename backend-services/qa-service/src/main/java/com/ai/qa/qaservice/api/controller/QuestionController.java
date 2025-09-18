package com.ai.qa.qaservice.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ai.qa.qaservice.api.client.UserServiceClient;
import com.ai.qa.qaservice.api.dto.ApiResponse;
import com.ai.qa.qaservice.api.dto.HistoryRequest;
import com.ai.qa.qaservice.api.dto.QuestionRequest;
import com.ai.qa.qaservice.application.service.QaService;
import com.ai.qa.qaservice.domain.entity.QaHistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QuestionController {

        @Autowired
        private QaService qaService;
        private final UserServiceClient userServiceClient;

        /**
         * 处理用户提问
         */
        @PostMapping("/question")
        public ResponseEntity<ApiResponse<QaHistory>> askQuestion(@RequestBody QuestionRequest dto) {
                log.info("========= Received question from user: " + dto.getUserId() + ", question: "
                                + dto.getQuestion());
                QaHistory result = qaService.processQuestion(dto.getUserId(), dto.getQuestion());
                return ResponseEntity.ok(ApiResponse.success(result));
        }

        /**
         * 获取用户最近的问答历史
         */
        @GetMapping("/history")
        public ResponseEntity<ApiResponse<List<QaHistory>>> getHistory(
                        @RequestBody HistoryRequest dto) {
                log.info("========= getHistory userId: {}, limit: {} =========", dto.getUserId(), dto.getLimit());
                List<QaHistory> history = qaService.getRecentHistory(dto.getUserId(), dto.getLimit());
                return ResponseEntity.ok(ApiResponse.success(history));
        }

        /**
         * 获取用户姓名
         */
        @GetMapping("/getUserName")
        public ResponseEntity<ApiResponse<String>> getUserName(@RequestParam("userId") Long userId) {
                log.info("========= getUserName userId: {} =========", userId);
                return userServiceClient.getUserName(userId);
        }

        /**
         * 删除指定用户的历史记录
         */
        @PostMapping("/deleteHistory")
        public ResponseEntity<ApiResponse<Boolean>> deleteHistory(@RequestParam("userId") Long userId) {
                boolean result = qaService.deleteHistory(userId) > 0 ? true : false;
                return ResponseEntity.ok(ApiResponse.success(result));
        }

}
