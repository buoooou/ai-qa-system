package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.CreateSessionRequest;
import com.ai.qa.service.api.dto.QAHistoryRequest;
import com.ai.qa.service.api.dto.QARequest;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistorySessionDTO;
import com.ai.qa.service.application.service.QAHistoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "问答接口")
@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAHistoryService qaHistoryService;

    @Operation(summary = "测试Feign调用")
    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        return qaHistoryService.testFeign();
    }

    // 聊天会话管理（创建会话SessionID)
    @Operation(summary = "创建会话")
    @PostMapping("/sessions")
    public ResponseEntity<QAHistorySessionDTO> createSession(
            @Parameter(description = "用户ID", required = true) CreateSessionRequest request) {
        String userId = request.getUserId();
        if(userId == null || userId.length() == 0) {
            throw new RuntimeException("用户ID不能为空");
        }

        QAHistorySessionDTO dto = qaHistoryService.initQAHistory(userId);

        return ResponseEntity.ok(dto);
    }

    // 消息交互(同时保存)
    @Operation(summary = "发送消息并保存记录")
    @PostMapping("/messages")
    public ResponseEntity<String> sendMessage(@RequestBody QARequest request) {
        String sessionId = request.getSessionId();
        String userId = request.getUserId();
        String question = request.getQuestion();
        if(sessionId == null || sessionId.length() == 0 || userId == null || userId.length() == 0) {
            return ResponseEntity.badRequest().body("会话ID或用户ID不能为空");
        }
        if(question == null || question.length() == 0){
            return ResponseEntity.badRequest().body("问题不能为空");
        }

        try{
            // Todo
            // 请求 AI 服务获取回复
            String aiResponse = "AI 回复消息";

            // 保存聊天记录
            qaHistoryService.saveHistory(sessionId, userId, question, aiResponse);
            // Todo 发送消息并获取 AI 回复 
            return ResponseEntity.ok("AI 回复消息");
        } catch(Exception ex){
            throw new RuntimeException("内部处理异常");
        }
    }

    @GetMapping("/sessions/qahistory")
    public ResponseEntity<QAHistoryDTO> getChatSession(@RequestBody QAHistoryRequest request) {
        String sessionId = request.getSessionId();
        String userId = request.getUserId();
        if(sessionId == null || sessionId.length() == 0 || userId == null || userId.length() == 0) {
            return ResponseEntity.badRequest().body(null);
        }
        QAHistoryDTO dto = qaHistoryService.getHistoryBySessionId(userId, sessionId);
        // 获取指定会话的聊天记录
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<String> deleteChatSession(@PathVariable String sessionId) {
        int delResult = qaHistoryService.delHistory(sessionId);
        if(delResult == 1){
            // 结束并删除会话
            return ResponseEntity.ok("会话删除成功");
        } else {
            return ResponseEntity.badRequest().body("会话不存在");
        }
    }



    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<String> getChatMessages(@PathVariable String sessionId) {
        // 获取会话中的历史消息
        return ResponseEntity.ok("历史消息列表");
    }
}
