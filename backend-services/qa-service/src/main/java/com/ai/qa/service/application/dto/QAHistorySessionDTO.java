package com.ai.qa.service.application.dto;

import java.util.ArrayList;
import java.util.List;

import com.ai.qa.service.domain.model.QAHistorySession;

import lombok.Data;

/**
 * 历史会话数据传输对象，用于封装历史会话的相关信息。
 */
@Data
public class QAHistorySessionDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 历史会话ID列表
     */
    private List<String> historySessionId;
    /**
     * 历史会话主题列表
     */
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
