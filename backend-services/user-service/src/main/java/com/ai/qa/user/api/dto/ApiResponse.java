package com.ai.qa.user.api.dto;

import com.ai.qa.user.api.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

// @JsonInclude(JsonInclude.Include.NON_NULL) // 序列化时忽略null值的字段
public class ApiResponse<T> implements Serializable {

    private final int code;
    private final String message;
    private final boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL) // 仅在data不为null时序列化
    private T data;

    private ApiResponse(int code, String message, boolean success, T data) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.data = data;
    }

    // --- 静态工厂方法 ---

    // 成功的响应（带数据）
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), true, data);
    }

    // 成功的响应（不带数据）
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    // 失败的响应
    public static <T> ApiResponse<T> failure(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), false, null);
    }

    // 失败的响应（可自定义覆盖错误信息）
    public static <T> ApiResponse<T> failure(ErrorCode errorCode, String message) {
        return new ApiResponse<>(errorCode.getCode(), message, false, null);
    }

    // --- Getters ---
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }
}
