package com.ai.qa.user.api.dto;

import com.ai.qa.user.api.exception.ErrCode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/*
返回结果DTO
 */
@Data
public class UserResponseDto {
    // getter and setter
    private int code;
    private String message;
    private Object data;

    public UserResponseDto(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static UserResponseDto success(Object data) {
        return new UserResponseDto(ErrCode.SUCCESS.getCode(), ErrCode.SUCCESS.getMsg(), data);
    }

    public static UserResponseDto fail(ErrCode errCode) {
        return new UserResponseDto(errCode.getCode(), errCode.getMsg(), null);
    }

}
