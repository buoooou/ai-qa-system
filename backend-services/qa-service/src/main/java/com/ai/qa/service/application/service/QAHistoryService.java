package com.ai.qa.service.application.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistorySessionDTO;
import com.ai.qa.service.domain.exception.QAHistoryNotFoundException;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.domain.service.QAService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QAHistoryService {

    private final QAService qaService;

    public String testFeign() {
        return qaService.processQuestion(1L);
    }

    public QAHistorySessionDTO initQAHistory(String userId) {
        // 创建SessionId
        String sessionId = qaService.createSessionId(userId);
        // 获取历史SessionId列表
        List<QAHistorySession> qaHistorySessionList = qaService.getHistorySessionId(userId);
        return QAHistorySessionDTO.fromDomain(userId, sessionId, qaHistorySessionList);
    }

    public void saveHistory(String userId, String sessionId, String question, String answer) {
        qaService.saveQAHistory(userId, sessionId, question, answer, LocalDateTime.now());
    }


    public int delHistory(String sessionId) {
        return qaService.delQAHistory(sessionId);
    }

    public QAHistoryDTO getHistoryBySessionId(String userId, String sessionId) {

        List<QAHistory> qaHistoryList = qaService.getHistoryBySessionId(sessionId);
        if(qaHistoryList.isEmpty()) {
            throw new QAHistoryNotFoundException(sessionId);
        }

        return QAHistoryDTO.fromDomain(userId, sessionId, qaHistoryList);
    }
}
