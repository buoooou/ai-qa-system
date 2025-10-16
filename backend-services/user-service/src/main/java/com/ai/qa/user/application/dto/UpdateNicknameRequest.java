package com.ai.qa.user.application.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateNicknameRequest {

    @NotBlank
    @Size(max = 64)
    private String nickname;
}
