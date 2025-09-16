package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.service.QAHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qa")
public class QAController {

    @Autowired
    private QAHistoryService qaHistoryService;

    @PostMapping("/save")
    public ResponseEntity<QAHistoryDTO> saveHistory(@RequestBody SaveHistoryCommand command){
        QAHistoryDTO dto = qaHistoryService.saveHistory(command);
        return ResponseEntity.ok(dto);
    }
}