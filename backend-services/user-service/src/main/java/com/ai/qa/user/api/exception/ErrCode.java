package com.ai.qa.user.api.exception;


import lombok.Getter;

@Getter
public enum ErrCode {
    SUCCESS(200, "成功"),
    USERNAME_EXISTS(1001, "用户名已存在"),
    USERNAME_NOT_FOUND(1002, "用户不存在"),
    INVALID_CREDENTIALS(1003, "用户名或密码错误"),
    USERNAME_REQUIRED(1004, "用户名不能为空"),
    PASSWORD_REQUIRED(1005, "密码不能为空"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String msg;

    ErrCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
