package com.ai.qa.user.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理IllegalArgumentException异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * 处理NoSuchElementException异常
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * 处理IllegalStateException异常
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误", request);
    }

    /**
     * 构建错误响应
     */
    private ResponseEntity<?> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(errorDetails, status);
    }
}