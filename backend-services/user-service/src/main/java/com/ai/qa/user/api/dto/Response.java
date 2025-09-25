package com.ai.qa.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private String result; // SUCCESS/ERROR
    private String message;
    private T data;

    public static <T> Response<T> success(T data) {
        return new Response<>("SUCCESS", "操作成功", data);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>("ERROR", message, null);
    }
}