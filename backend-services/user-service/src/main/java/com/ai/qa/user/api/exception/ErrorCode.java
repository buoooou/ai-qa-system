package com.ai.qa.user.api.exception;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import com.ai.qa.user.common.Constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // --- 成功 ---
    SUCCESS(HttpStatus.OK, 200, Constants.MSG_RES_SUCCESS),
    CREATED(HttpStatus.CREATED, 201, Constants.MSG_USER_REGISTER_SUCCESS),
    // --- 身份验证失败 ---
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, Constants.MSG_USER_AUTHENTICATE_FAIL),
    // --- 客户端错误 (4XX) ---
    USER_ALREADY_EXIST(HttpStatus.CONFLICT, 4009, Constants.MSG_USER_ALREADY_EXIST),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, Constants.MSG_USER_BAD_REQUEST),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, Constants.MSG_USER_NOT_FOUND),
    NICKNAME_IS_EMPTY(HttpStatus.BAD_REQUEST, 4001, Constants.MSG_NICKNAME_IS_EMPTY),
    NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, 4002, Constants.MSG_NICKNAME_TOO_LONG),
    NICKNAME_UNCHANGED(HttpStatus.BAD_REQUEST, 4003, Constants.MSG_NICKNAME_UNCHANGED),
    USERNAME_IS_EMPTY(HttpStatus.BAD_REQUEST, 4004, Constants.MSG_USERNAME_IS_EMPTY),
    PASSWORD_IS_EMPTY(HttpStatus.BAD_REQUEST, 4005, Constants.MSG_PASSWORD_IS_EMPTY),
    PASSWORD_CONFIRM_ERROR(HttpStatus.BAD_REQUEST, 4006, Constants.MSG_PASSWORD_CONFIRM_ERROR),
    CHANGE_PASSWORD_FAIL(HttpStatus.BAD_REQUEST, 4007, Constants.MSG_CHANGE_PASSWORD_FAIL),
    OLD_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, 4008, Constants.MSG_OLD_PASSWORD_INCORRECT),
    // --- 服务器错误 (5XX) ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, Constants.MSG_GLOBAL_INTERNAL_ERROR);

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public static ErrorCode valueFrom(String message) {
        return Arrays.stream(ErrorCode.values()).filter(errorCode -> errorCode.getMessage().equals(message)).findFirst().orElseThrow(() -> new IllegalStateException("未找到匹配状态"));
    }
}
