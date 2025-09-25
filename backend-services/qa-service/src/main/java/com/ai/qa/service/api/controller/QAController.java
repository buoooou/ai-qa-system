package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDto;
import com.ai.qa.service.api.dto.SaveHistoryRequest;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.service.QAService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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
    public ResponseEntity<QAHistoryDto> saveHistory(@RequestBody SaveHistoryRequest request){
        request.getUserId();
        SaveHistoryCommand command = new SaveHistoryCommand( request.getUserId(),request.getQuestion(), request.getAnswer());
        QAHistoryDto dto = qaHistoryService.saveHistory(command);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/history")
    public ResponseEntity<List<QAHistoryDto>> queryHistory(
            @RequestParam Long userId) {
        QAHistoryQuery query = new QAHistoryQuery();
        query.setUserId(userId);

        return ResponseEntity.ok(qaHistoryService.queryUserHistory(query));
    }
    /**
     * 健康检查接口
     * 
     * 用于检查用户服务是否正常运行
     * 
     * @return Response<String> 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Service is running\", \"User Service is running");
    }
}
