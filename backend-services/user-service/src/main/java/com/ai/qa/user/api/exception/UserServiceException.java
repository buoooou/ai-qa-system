package com.ai.qa.user.api.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {

    private final int code;

    public UserServiceException(int errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public UserServiceException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
    }
}
