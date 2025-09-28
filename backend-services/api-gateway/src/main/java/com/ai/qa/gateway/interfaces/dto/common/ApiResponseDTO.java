package com.ai.qa.gateway.interfaces.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDTO<T> {
    private int code;
    private String message;
    private boolean success;
    private T data;
}
