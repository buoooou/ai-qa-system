package com.ai.qa.user.api.exception;

public enum ErrCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 通用错误
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "访问被禁止"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    // 用户相关错误
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_ALREADY_EXISTS(1002, "用户名已存在"),
    INVALID_USERNAME_OR_PASSWORD(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    
    // JWT相关错误
    TOKEN_EXPIRED(2001, "Token已过期"),
    TOKEN_INVALID(2002, "Token无效"),
    TOKEN_MISSING(2003, "Token缺失"),
    
    // 验证相关错误
    VALIDATION_ERROR(3001, "数据验证失败");

    private final int code;
    private final String message;
    
    ErrCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
