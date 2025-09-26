package com.ai.qa.qaservice.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ai.qa.qaservice.application.model.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        log.error("handleBusinessException 业务异常: {}", ex.getMessage());
        ApiResponse<Object> apiResponse = ApiResponse.error(ex.getStatus().value(), ex.getMessage());
        return new ResponseEntity<>(apiResponse, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex) {
        log.error("handleGlobalException 未捕获的系统异常: {}", ex.getMessage(), ex);
        ApiResponse<Object> apiResponse = ApiResponse.error(500, ex.getMessage());
        return new ResponseEntity<>(apiResponse, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}