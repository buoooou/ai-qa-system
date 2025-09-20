package com.ai.qa.qaservice.api.controller;

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

        private final QaService qaService;
        private final UserServiceClient userServiceClient;

        /**
         * 处理用户提问（支持指定对话ID或创建新对话）
         */
        @PostMapping("/question")
        public ResponseEntity<ApiResponse<QaHistory>> askQuestion(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId,
                        @RequestBody QuestionRequest dto) {

                log.info("Received question from user: {}, conversation: {}, question: {}",
                                userId, dto.getConversationId(), dto.getQuestion());

                QaHistory result = qaService.processQuestion(userId, dto.getConversationId(), dto.getQuestion());
                return ResponseEntity.ok(ApiResponse.success(result));
        }

        /**
         * 获取指定对话的历史记录
         */
        @GetMapping("/conversation/history")
        public ResponseEntity<ApiResponse<List<QaHistory>>> getConversationHistory(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId,
                        @RequestParam String conversationId) {

                log.info("Getting history for user: {}, conversation: {}", userId, conversationId);
                List<QaHistory> history = qaService.getConversationHistory(userId, conversationId);
                return ResponseEntity.ok(ApiResponse.success(history));
        }

        /**
         * 获取所有对话的历史记录
         */
        @GetMapping("/conversations/allHistory")
        public ResponseEntity<ApiResponse<List<QaHistory>>> getConversationAllHistory(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId) {

                log.info("Getting all conversations for user: {}", userId);
                List<QaHistory> conversations = qaService.getConversationAllHistory(userId);
                return ResponseEntity.ok(ApiResponse.success(conversations));
        }

        /**
         * 删除指定对话
         */
        @DeleteMapping("/conversation/delete")
        public ResponseEntity<ApiResponse<Boolean>> deleteConversation(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId,
                        @RequestParam String conversationId) {

                log.info("Deleting conversation: {} for user: {}", conversationId, userId);
                int deleted = qaService.deleteConversation(userId, conversationId);
                return ResponseEntity.ok(ApiResponse.success(deleted > 0));
        }

        /**
         * 删除用户所有对话
         */
        @DeleteMapping("/conversations/deleteAll")
        public ResponseEntity<ApiResponse<Boolean>> deleteAllConversations(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId) {

                log.info("Deleting all conversations for user: {}", userId);
                int deleted = qaService.deleteAllConversations(userId);
                return ResponseEntity.ok(ApiResponse.success(deleted > 0));
        }

        @GetMapping("/getUserName")
        public ResponseEntity<ApiResponse<String>> getUserName(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId) {
                log.info("getUserName userId: {} ", userId);
                return userServiceClient.getUserName(userId);
        }

        @GetMapping("/conversations/IDList")
        public ResponseEntity<ApiResponse<List<String>>> getUserConversationsIDList(
                        @RequestHeader(value = "X-User-Id", required = true) Long userId) {
                log.info("getUserConversations userId: {} ", userId);
                List<String> conversations = qaService.getUserConversationsID(userId).stream().distinct().toList();
                return ResponseEntity.ok(ApiResponse.success(conversations));
        }
}