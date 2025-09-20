   
package com.ai.qa.service.application.dto;   
import lombok.Data;
import lombok.Getter;

@Data
public class SaveHistoryCommand {
    private Long userId;
    private String question;
    private String answer;
    private String sessionId;

    public SaveHistoryCommand(Long userId, String question, String answer) {
    	this.userId = userId;
        this.question = question;    
        this.answer = answer;   
    }

}