package com.ai.qa.user.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    SUCCESS(HttpStatus.OK, 200, "操作成功"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "错误的请求"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "未授权"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "无权限"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "用户不存在"),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, 4041, "会话不存在"),
    SESSION_ALREADY_EXISTS(HttpStatus.CONFLICT, 409, "会话ID已存在"),

    NICKNAME_IS_EMPTY(HttpStatus.BAD_REQUEST, 1001, "昵称不能为空"),
    NICKNAME_TOO_LONG(HttpStatus.BAD_REQUEST, 1002, "昵称长度不能超过50个字符"),
    NICKNAME_UNCHANGED(HttpStatus.BAD_REQUEST, 1003, "新昵称不能与旧昵称相同"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "服务器内部错误");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
