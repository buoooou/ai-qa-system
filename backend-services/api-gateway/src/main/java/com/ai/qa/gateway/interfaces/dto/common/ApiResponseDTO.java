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

    public static <T> ApiResponseDTO<T> success(T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setCode(0);
        response.setMessage("success");
        response.setSuccess(true);
        response.setData(data);
        return response;
    }
}
