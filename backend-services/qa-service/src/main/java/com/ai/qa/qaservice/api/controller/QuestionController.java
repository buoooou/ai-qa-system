package com.ai.qa.qaservice.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ai.qa.qaservice.api.client.UserServiceClient;
import com.ai.qa.qaservice.api.dto.ApiResponse;
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
        public ResponseEntity<ApiResponse<QaHistory>> askQuestion(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId,
                        @RequestBody QuestionRequest dto) {
                log.info("========= Received question from user: " + userId + ", question: "
                                + dto.getQuestion());
                QaHistory result = qaService.processQuestion(userId, dto.getQuestion());
                return ResponseEntity.ok(ApiResponse.success(result));
        }

        /**
         * 获取用户最近的问答历史
         */
        @GetMapping("/history")
        public ResponseEntity<ApiResponse<List<QaHistory>>> getHistory(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId,
                        @RequestParam(defaultValue = "5") Integer limit) {
                log.info("========= getHistory userId: {}, limit: {} =========", userId, limit);
                List<QaHistory> history = qaService.getRecentHistory(userId, limit);
                return ResponseEntity.ok(ApiResponse.success(history));
        }

        /**
         * 获取用户姓名
         */
        @GetMapping("/getUserName")
        public ResponseEntity<ApiResponse<String>> getUserName(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId) {
                log.info("========= getUserName userId: {} =========", userId);
                return userServiceClient.getUserName(userId);
        }

        /**
         * 删除指定用户的历史记录
         */
        @PostMapping("/deleteHistory")
        public ResponseEntity<ApiResponse<Boolean>> deleteHistory(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId) {
                boolean result = qaService.deleteHistory(userId) > 0 ? true : false;
                return ResponseEntity.ok(ApiResponse.success(result));
        }

}
