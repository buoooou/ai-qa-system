package com.ai.qa.service.api.dto;

import com.ai.qa.service.application.dto.SaveHistoryCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveHistoryRequest {

    @NotNull
    private Long sessionId;

    @NotNull
    private Long userId;

    @NotBlank
    private String question;

    private String answer;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer latencyMs;

    public SaveHistoryCommand toCommand() {
        return SaveHistoryCommand.builder()
                .sessionId(sessionId)
                .userId(userId)
                .question(question)
                .answer(answer)
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .latencyMs(latencyMs)
                .build();
    }
}
