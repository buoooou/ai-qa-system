package com.ai.qa.user.api.dto;

import com.ai.qa.user.api.exception.ErrCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * API返回结果Dto
 * @param <T>
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto<T> {
    private int code;
    private String message;
    private T data;

    public UserResponseDto(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> UserResponseDto<T> success(T data) {
        return new UserResponseDto<>(ErrCode.SUCCESS.getCode(), ErrCode.SUCCESS.getMsg(), data);
    }

    public static <T> UserResponseDto<T> fail(ErrCode errCode) {
        return new UserResponseDto<>(errCode.getCode(), errCode.getMsg(), null);
    }

}