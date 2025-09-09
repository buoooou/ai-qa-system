package com.ai.qa.user.api.dto;

import lombok.Data;

@Data
public class Response<T> {
    private final int code;
    private final String message;
    private final T data;

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "Success", data);
    }

    public static Response<Void> error(int code, String message) {
        return new Response<>(code, message, null);
    }
}
