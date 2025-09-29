package com.ai.qa.service.domain.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class QAHistory {

    private Long id;
    private String userId;
    private String question;
    private String answer;
    private String sessionId;
    private LocalDateTime createTime;

//    private Object rag;

    public Long getId() {
        return this.id;
    }

    /**
     *
     * @param question
     * @return
     */
    // public String getAnswer(String question) {
    //     String response = rag.getContext();
    //     return answer+response;
    // }

    public String getUserId() {
        return this.userId;
    }

    // public String getRAGAnswer() {
    //     getAnswer();
    //     serivice.sss();
    //     return "";
    // }
    
    public static QAHistory createNew(String userId, String question, String answer,String sessionId) {
        QAHistory qaHistory = new QAHistory();
        qaHistory.userId = userId;
        qaHistory.question = question;
        qaHistory.answer = answer;
        qaHistory.sessionId = sessionId;
        qaHistory.createTime = LocalDateTime.now();
        return qaHistory;
    }
}
