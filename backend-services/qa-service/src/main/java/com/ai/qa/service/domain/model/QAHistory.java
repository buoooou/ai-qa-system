package com.ai.qa.service.domain.model;

import java.time.LocalDateTime;

public class QAHistory {

    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public QAHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public static QAHistory createNew(Long userId, String question, String answer) {
        QAHistory history = new QAHistory();
        history.setUserId(userId);
        history.setQuestion(question);
        history.setAnswer(answer);
        history.setCreateTime(LocalDateTime.now());
        return history;
    }
}