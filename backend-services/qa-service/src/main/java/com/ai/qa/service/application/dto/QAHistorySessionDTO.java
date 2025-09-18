package com.ai.qa.service.application.dto;

import java.util.ArrayList;
import java.util.List;

import com.ai.qa.service.domain.model.QAHistorySession;

import lombok.Data;

@Data
public class QAHistorySessionDTO {
    private String userId;
    private String sessionId;
    private List<String> historySessionId;
    private List<String> historySessionTopic;

    private QAHistorySessionDTO() {

    }

    public static QAHistorySessionDTO fromDomain(String userId, String sessionId, List<QAHistorySession> historySessionList){
        List<String> historySessionId = new ArrayList<>();
        List<String> historySessionTopic = new ArrayList<>();
        for (QAHistorySession qaHistorySession : historySessionList) {
            historySessionId.add(qaHistorySession.getSessionId());
            historySessionTopic.add(qaHistorySession.getTopic());
        }
        QAHistorySessionDTO dto = new QAHistorySessionDTO();
        dto.setUserId(userId);
        dto.setSessionId(sessionId);
        dto.setHistorySessionId(historySessionId);
        dto.setHistorySessionTopic(historySessionTopic);
        return dto;
    }
}
