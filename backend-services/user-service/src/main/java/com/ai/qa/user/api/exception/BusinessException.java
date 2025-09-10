package com.ai.qa.user.api.exception;

import lombok.Getter;

/**
 * 业务异常封装
 * 提供错误码与错误信息，支持链式异常
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer errorCode;
    private final String errorMessage;

    public BusinessException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(Integer errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /* ---------- 快捷工厂 ---------- */

    public static BusinessException userNotFound() {
        return new BusinessException(ErrCode.USER_NOT_FOUND, ErrCode.MSG_USER_NOT_FOUND);
    }

    public static BusinessException userAlreadyExists() {
        return new BusinessException(ErrCode.USER_ALREADY_EXISTS, ErrCode.MSG_USER_ALREADY_EXISTS);
    }

    public static BusinessException passwordIncorrect() {
        return new BusinessException(ErrCode.PASSWORD_INCORRECT, ErrCode.MSG_PASSWORD_INCORRECT);
    }

    public static BusinessException passwordMismatch() {
        return new BusinessException(ErrCode.PASSWORD_MISMATCH, ErrCode.MSG_PASSWORD_MISMATCH);
    }

    public static BusinessException invalidToken() {
        return new BusinessException(ErrCode.INVALID_TOKEN, ErrCode.MSG_INVALID_TOKEN);
    }

    public static BusinessException badRequest(String message) {
        return new BusinessException(ErrCode.BAD_REQUEST, message);
    }

    public static BusinessException of(Integer errorCode, String errorMessage) {
        return new BusinessException(errorCode, errorMessage);
    }

    public static BusinessException of(Integer errorCode, String errorMessage, Throwable cause) {
        return new BusinessException(errorCode, errorMessage, cause);
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }

    public String getDetailMessage() {
        return "ErrorCode: " + errorCode + ", Message: " + errorMessage;
    }
}