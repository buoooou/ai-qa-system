package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.service.QAService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;

import java.util.List;

import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/qa")
@RequiredArgsConstructor
public class QAController {
    private final QAService qaService;
    private final QAHistoryService qaHistoryService;
    private final QAHistoryMapper qaHistoryMapper;

    @GetMapping("/test")
    public String testFeign() {
        System.out.println("测试feign");
        return qaService.processQuestion(1L);
    }

    @PostMapping("/save")
    @Operation(summary = "保存历史请求", description = "保存历史记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册登录",
             content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = SaveHistoryRequest.class)))
    })
    public ResponseEntity<QAHistoryDTO> saveHistory(@RequestBody SaveHistoryRequest request){
        String userId = request.getUserId();
        if (userId != null && userId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        QAHistoryDTO dto= qaHistoryService.saveHistory(request);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "获取历史列表请求", description = "根据用户ID获取历史列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册登录")
    })
    public ResponseEntity<List<QAHistoryDTO>> getByUserId(@PathVariable("username") String userId) {
        if (userId != null && userId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        List<QAHistoryDTO> dtoList = qaHistoryService.queryUserHistoryByUserId(userId);
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/gethistory/{sessionid}")
    @Operation(summary = "获取历史列表请求", description = "根据会话ID获取历史列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册登录")
    })
    public ResponseEntity<List<QAHistoryDTO>> getBySessionId(@PathVariable("sessionid") String sessionId) {
        if (sessionId != null && sessionId.isEmpty()) {
            throw new IllegalArgumentException("会话ID不能为空");
        }
        List<QAHistoryDTO> dtoList = qaHistoryService.queryUserHistoryByUserId(sessionId);
        return ResponseEntity.ok(dtoList); 
    }
}
