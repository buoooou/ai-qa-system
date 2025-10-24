package com.ai.qa.service.api.exception;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import com.ai.qa.service.common.Constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // --- 成功 ---
    SUCCESS(HttpStatus.OK, 200, Constants.MSG_RES_SUCCESS),
    // --- 客户端错误 (4XX) ---
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, Constants.MSG_USER_BAD_REQUEST),
    USERID_EMPTY(HttpStatus.BAD_REQUEST, 4001, Constants.MSG_BAD_REQUEST_USERID_EMPTY),
    SESSIONID_EMPTY(HttpStatus.BAD_REQUEST, 4002, Constants.MSG_BAD_REQUEST_SESSIONID_EMPTY),
    QUESTION_EMPTY(HttpStatus.BAD_REQUEST, 4003, Constants.MSG_BAD_REQUEST_QUESTION_EMPTY),
    ANSWER_EMPTY(HttpStatus.BAD_REQUEST, 4004, Constants.MSG_BAD_REQUEST_ANSWER_EMPTY),
    QA_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, 4005, Constants.MSG_QA_HISTORY_NOT_FOUND),
    // --- 服务器错误 (5XX) ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, Constants.MSG_GLOBAL_INTERNAL_ERROR),
    FETCH_QA_HISTORY_FIAL(HttpStatus.INTERNAL_SERVER_ERROR, 5001, Constants.MSG_FETCH_QA_HISTORY_FAIL),
    DELETE_QA_HISTORY_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 5002, Constants.MSG_DELETE_QA_HISTORY_FAIL),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 503, Constants.MSG_GLOBAL_SERVICE_UNAVAILABLE);

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ErrorCode(final HttpStatus httpStatus, final int code, final String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    public static ErrorCode valueFrom(final String message) {
        return Arrays.stream(ErrorCode.values()).filter(errorCode -> errorCode.getMessage().equals(message)).findFirst().orElseThrow(() -> new IllegalStateException("未找到匹配状态"));
    }
}
