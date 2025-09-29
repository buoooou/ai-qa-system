package com.ai.qa.gateway.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserServiceApiResponseDTO<T> {
    private int code;
    private String message;
    private boolean success;
    private T data;
}
