package com.ai.qa.service.application.dto;

import java.util.ArrayList;
import java.util.List;

import com.ai.qa.service.domain.model.QAHistory;

import lombok.Data;

@Data
public class QAHistoryDTO {
    // 当前会话的SessionId
    private String sessionId;
    // 当前会话的userId
    private String userId;
    private List<String> question;
    private List<String> answer;

    private QAHistoryDTO() {
    }

    public static QAHistoryDTO fromDomain(String userId, String sessionId, List<QAHistory> historyList){
        List<String> question = new ArrayList<>();
        List<String> answer = new ArrayList<>();
        for (QAHistory qaHistory : historyList) {
            question.add(qaHistory.getQuestion());
            answer.add(qaHistory.getAnswer());
        }
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setUserId(userId);
        dto.setSessionId(sessionId);
        dto.setQuestion(question);
        dto.setAnswer(answer);
        return dto;
    }
}
