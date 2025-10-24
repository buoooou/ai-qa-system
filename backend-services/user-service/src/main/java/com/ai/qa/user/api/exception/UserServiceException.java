package com.ai.qa.user.api.exception;

import lombok.Getter;

@Getter
public class UserServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public UserServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public UserServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
