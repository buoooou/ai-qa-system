package com.ai.qa.service.api.dto;

import java.io.Serializable;

import com.ai.qa.service.api.exception.ErrorCode;
import com.ai.qa.service.common.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "统一API响应")
public class ApiResponse<T> implements Serializable {

    @Schema(description = "HTTP Status Code")
    private int code;

    @Schema(description = "业务消息")
    private String message;

    @Schema(description = "响应数据")
    @JsonInclude(JsonInclude.Include.NON_NULL) // 仅在data不为null时序列化
    private T data;

    private boolean success;

    public static <T> ApiResponse<T> success(int code, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(Constants.MSG_RES_SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(errorCode.getCode())
                .message(message)
                .data(null)
                .build();
    }

}
