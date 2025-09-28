package com.ai.qa.user.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSessionRequest {

    @Size(max = 255)
    private String title;
}
