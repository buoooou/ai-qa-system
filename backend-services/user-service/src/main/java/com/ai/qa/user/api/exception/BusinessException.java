package com.ai.qa.user.api.exception;

public class BusinessException extends RuntimeException {
    
    private int code;
    private String message;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(ErrCode errCode) {
        super(errCode.getMessage());
        this.code = errCode.getCode();
        this.message = errCode.getMessage();
    }

    public BusinessException(ErrCode errCode, String customMessage) {
        super(customMessage);
        this.code = errCode.getCode();
        this.message = customMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}