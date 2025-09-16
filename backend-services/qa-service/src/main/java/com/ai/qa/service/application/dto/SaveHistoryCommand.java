package com.ai.qa.service.application.dto;

public class SaveHistoryCommand {
    private Long userId;
    private String question;
    private String answer;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}