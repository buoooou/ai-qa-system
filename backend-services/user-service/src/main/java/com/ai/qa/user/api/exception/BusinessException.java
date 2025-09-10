package com.ai.qa.user.api.exception;

import lombok.Getter;

/**
 * 业务异常处理
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrCode errCode;

    public BusinessException(ErrCode errCode) {
        super(errCode.getMsg());
        this.errCode = errCode;
    }

}