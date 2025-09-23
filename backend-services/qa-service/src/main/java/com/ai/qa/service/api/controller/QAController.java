package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.ChatRequest;
import com.ai.qa.service.api.dto.ChatResponse;
import com.ai.qa.service.api.dto.CreateNewSessionRequest;
import com.ai.qa.service.api.dto.CreateSessionRequest;
import com.ai.qa.service.api.dto.DelQAHistoryRequest;
import com.ai.qa.service.api.dto.LogoutRequest;
import com.ai.qa.service.api.dto.QAHistoryRequest;
import com.ai.qa.service.api.dto.QARequest;
import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistorySessionDTO;
import com.ai.qa.service.application.service.Gemini2Service;
import com.ai.qa.service.application.service.QAHistoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

/**
 * QA服务控制器，提供问答相关的API接口。
 * 包括会话管理、历史记录查询、问答交互等功能。
 */
import org.springframework.web.bind.annotation.*;

@Tag(name = "QA API", description = "问答服务接口")
@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {

    private final QAHistoryService qaHistoryService;
    private final Gemini2Service gemini2Service;

    /**
     * 创建会话，生成唯一的会话ID。
     *
     * @param request 包含用户ID的请求体
     * @return 包含会话ID的响应实体
     * @throws IllegalArgumentException 如果用户ID为空
     */
    @Operation(summary = "创建会话")
    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(@RequestBody CreateSessionRequest request) {
        String userid = request.getUserid();
        if(userid == null || userid.length() == 0) {
            return ResponseEntity.badRequest().body("用户ID不能为空");
        }

        QAHistorySessionDTO dto = qaHistoryService.initQAHistory(userid);

        return ResponseEntity.ok(dto);
    }

    /**
     * 新建会话，生成新的会话ID并保存主题（如果提供）。
     *
     * @param request 包含会话ID、用户ID和主题的请求体
     * @return 包含新会话ID的响应实体
     * @throws IllegalArgumentException 如果会话ID或用户ID为空
     */
    @Operation(summary = "新建会话")
    @PostMapping("/sessions/new")
    public ResponseEntity<?> createNewSession(@RequestBody CreateNewSessionRequest request) {
        String sessionId = request.getSessionId();
        String userid = request.getUserid();
        String topic = request.getTopic();
        if(sessionId == null || sessionId.length() == 0 || userid == null || userid.length() == 0) {
            return ResponseEntity.badRequest().body("用户ID或会话ID不能为空");
        }
        
        // 上轮会话中用户无消息时，不保存主题
        if(topic != null &&  topic.length() != 0){
            // 如果是历史会话，则不保存主题，否则保存主题
            if(!qaHistoryService.existSessionId(sessionId)){
                qaHistoryService.createQATopic(userid, sessionId, topic);
            }
        }
        // 获取历史会话信息
        QAHistorySessionDTO dto = qaHistoryService.initQAHistory(userid);

        return ResponseEntity.ok(dto);
    }

    /**
     * 用户退出时删除缓存的会话ID。
     *
     * @param request 包含会话ID的请求体
     * @return 包含操作结果的响应实体
     * @throws IllegalArgumentException 如果会话ID为空
     * @throws Exception 如果删除会话ID时发生错误
     */
    @Operation(summary = "用户退出时删除缓存的会话ID")
    @DeleteMapping("/sessions/logout")
    public ResponseEntity<String> delSession(@RequestBody LogoutRequest request){
        String sessionId = request.getSessionId();
        if(sessionId == null || sessionId.length() == 0) {
            return ResponseEntity.badRequest().body("会话ID不能为空");
        }
        try{
            if(qaHistoryService.delSessionId(sessionId)){
                return ResponseEntity.ok("安全退出成功");
            } else {
                return ResponseEntity.badRequest().body("会话ID不存在");
            }
            
        } catch(Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(500).body("退出失败：" + ex.getMessage());
        }
    }

    /**
     * 发送消息并保存记录。
     *
     * @param request 包含消息内容和会话信息的请求体
     * @return 包含操作结果的响应实体
     * @throws Exception 如果消息发送或保存时发生错误
     */
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
            // 请求 AI 服务获取回复
            String aiResponse = gemini2Service.generate(question);
            System.out.println(aiResponse);

            // 保存聊天记录
            qaHistoryService.saveHistory(userId, sessionId, question, aiResponse);
            // Todo 发送消息并获取 AI 回复 
            return ResponseEntity.ok(aiResponse);
        } catch(Exception ex){
            ex.printStackTrace();
            return ResponseEntity.status(500).body("处理消息失败：" + ex.getMessage());
        }
    }

    /**
     * 获取指定会话的历史记录。
     *
     * @param request 包含会话ID和用户ID的请求体
     * @return 包含历史记录的响应实体
     * @throws IllegalArgumentException 如果会话ID或用户ID为空
     */
    @Operation(summary = "获取指定会话的历史记录")
    @PostMapping("/sessions/qahistory")
    public ResponseEntity<?> getChatSession(@RequestBody QAHistoryRequest request) {

        String sessionId = request.getSessionId();
        String userid = request.getUserid();
        String topic = request.getTopic();
        String specifySId = request.getSpecifySId();
        if(sessionId == null || sessionId.length() == 0 || userid == null || userid.length() == 0) {
            return ResponseEntity.badRequest().body("用户ID或会话ID不能为空");
        }
        if(specifySId == null || specifySId.length() == 0 ) {
            return ResponseEntity.badRequest().body("指定的历史会话ID不能为空");
        }
        // 本轮会话中用户无消息时，不保存主题
        if(topic != null &&  topic.length() != 0){
            qaHistoryService.createQATopic(userid, sessionId, topic);
        }
        // 获取指定会话的聊天记录
        QAHistoryDTO dto = qaHistoryService.getHistoryBySessionId(userid, sessionId, specifySId);
        return ResponseEntity.ok(dto);
    }

    /**
     * 删除指定的历史会话。
     *
     * @param request 包含会话ID的请求体
     * @return 包含操作结果的响应实体
     * @throws IllegalArgumentException 如果会话ID为空
     */
    @Operation(summary = "删除指定的历史会话")
    @DeleteMapping("/sessions")
    public ResponseEntity<String> deleteChatSession(@RequestBody DelQAHistoryRequest request) {
        String sessionId = request.getSessionId();
        if(sessionId == null || sessionId.length() == 0) {
            return ResponseEntity.badRequest().body("会话ID不能为空");
        }

        int delResult = qaHistoryService.delHistory(sessionId);
        if(delResult == 1){
            // 结束并删除会话
            return ResponseEntity.ok("会话删除成功");
        } else {
            return ResponseEntity.badRequest().body("会话不存在");
        }
    }

    // @GetMapping("/sessions/{sessionId}/messages")
    // public ResponseEntity<String> getChatMessages(@PathVariable String sessionId) {
    //     // 获取会话中的历史消息
    //     return ResponseEntity.ok("历史消息列表");
    // }
}
