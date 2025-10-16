package com.ai.qa.gateway.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceApiResponseDTO<T> {
    private int code;
    private String message;
    private boolean success;
    private T data;
}
