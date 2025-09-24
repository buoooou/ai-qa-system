package com.ai.qa.service.controller;

import com.ai.qa.service.dto.QuestionRequest;
import com.ai.qa.service.dto.QuestionResponse;
import com.ai.qa.service.dto.QAStatistics;
import com.ai.qa.service.dto.Response;
import com.ai.qa.service.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/qa")
@CrossOrigin(origins = "*")
public class QAController {

    @Autowired
    private QAService qaService;

    /**
     * 处理用户问题
     */
    @PostMapping("/question")
    public Response<QuestionResponse> askQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            QuestionResponse response = qaService.processQuestion(request);
            return Response.success(response);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "服务器内部错误");
        }
    }

    /**
     * 获取用户问答历史
     */
    @GetMapping("/history/user/{userId}")
    public Response<List<QuestionResponse>> getUserQuestionHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<QuestionResponse> history = qaService.getQuestionHistory(userId, page, size);
            return Response.success(history);
        } catch (Exception e) {
            return Response.error(500, "获取问答历史失败");
        }
    }

    /**
     * 获取会话问答历史
     */
    @GetMapping("/history/session/{sessionId}")
    public Response<List<QuestionResponse>> getSessionHistory(@PathVariable String sessionId) {
        try {
            List<QuestionResponse> history = qaService.getSessionHistory(sessionId);
            return Response.success(history);
        } catch (Exception e) {
            return Response.error(500, "获取会话历史失败");
        }
    }

    /**
     * 删除问答记录
     */
    @DeleteMapping("/{qaId}")
    public Response<Void> deleteQuestion(@PathVariable Long qaId) {
        try {
            qaService.deleteQuestion(qaId);
            return Response.success(null);
        } catch (IllegalArgumentException e) {
            return Response.error(400, e.getMessage());
        } catch (Exception e) {
            return Response.error(500, "删除问答记录失败");
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Response<String> health() {
        return Response.success("QA Service is running");
    }
}
