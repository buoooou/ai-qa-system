package com.ai.qa.service.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ai.qa.service.api.dto.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(IllegalArgumentException e) {
        log.error("[QA-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), IllegalArgumentException.class.getSimpleName(), e.getMessage());
        ErrorCode errorCode = ErrorCode.valueFrom(e.getMessage());
        ApiResponse<?> response = ApiResponse.error(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("[QA-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), EntityNotFoundException.class.getSimpleName(), e.getMessage());
        ApiResponse<?> response = ApiResponse.error(ErrorCode.BAD_REQUEST);
        return new ResponseEntity<>(response, ErrorCode.BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception e) {
        log.error("[QA-Service] [{}]## Unexpected exception occured. message:{}", this.getClass().getSimpleName(), e.getMessage());
        ApiResponse<?> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
