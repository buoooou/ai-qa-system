package com.ai.qa.qa.api.controller;

import com.ai.qa.qa.api.dto.QARequest;
import com.ai.qa.qa.api.dto.QAResponse;
import com.ai.qa.qa.api.dto.Response;
import com.ai.qa.qa.application.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/qa")
public class QAController {
    
    @Autowired
    private QAService qaService;
    
    @PostMapping("/ask")
    public Response<QAResponse> askQuestion(@Valid @RequestBody QARequest request) {
        QAResponse response = qaService.askQuestion(request);
        return Response.success(response);
    }
    
    @GetMapping("/history")
    public Response<List<QAResponse>> getQAHistory(@RequestParam Long userId) {
        List<QAResponse> history = qaService.getQAHistory(userId);
        return Response.success(history);
    }
    
    @GetMapping("/{id}")
    public Response<QAResponse> getQAById(@PathVariable Long id) {
        QAResponse response = new QAResponse();
        com.ai.qa.qa.domain.entity.QA qa = qaService.getQAById(id);
        if (qa != null) {
            response.setId(qa.getId());
            response.setQuestion(qa.getQuestion());
            response.setAnswer(qa.getAnswer());
            response.setCreateTime(qa.getCreateTime());
            return Response.success(response);
        } else {
            return Response.error("未找到指定的问答记录");
        }
    }
}