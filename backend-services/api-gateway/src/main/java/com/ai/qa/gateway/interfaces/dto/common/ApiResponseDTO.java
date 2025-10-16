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
        response.setCode(200);
        response.setMessage("success");
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDTO<T> failure(int code, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(false);
        response.setData(null);
        return response;
    }
}
