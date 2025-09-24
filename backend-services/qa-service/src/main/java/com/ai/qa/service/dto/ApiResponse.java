package com.ai.qa.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应格式
 *
 * 用于接收其他微服务的统一响应格式
 *
 * @author David
 * @version 1.0
 * @since 2025-09-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 是否错误
     */
    private Boolean error;

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(System.currentTimeMillis());
        response.setSuccess(true);
        response.setError(false);
        return response;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    /**
     * 错误响应（带错误码）
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(null);
        response.setTimestamp(System.currentTimeMillis());
        response.setSuccess(false);
        response.setError(true);
        return response;
    }

    /**
     * 错误响应（默认500错误码）
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }
}