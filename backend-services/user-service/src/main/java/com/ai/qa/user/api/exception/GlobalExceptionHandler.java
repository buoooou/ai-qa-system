package com.ai.qa.user.api.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ai.qa.user.api.dto.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(UserServiceException e) {
        log.error("[User-Service] [{}]## {} occured. ErrorCode:{}, message:{}", this.getClass().getSimpleName(), UserServiceException.class.getSimpleName(), e.getErrorCode().getCode(), e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<?> response = ApiResponse.error(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(IllegalArgumentException e) {
        log.error("[User-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), IllegalArgumentException.class.getSimpleName(), e.getMessage());
        ErrorCode errorCode = ErrorCode.valueFrom(e.getMessage());
        ApiResponse<?> response = ApiResponse.error(errorCode);
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(AuthenticationException e) {
        log.error("[User-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), AuthenticationException.class.getSimpleName(), e.getMessage());
        ApiResponse<?> response = ApiResponse.error(ErrorCode.UNAUTHORIZED);
        return new ResponseEntity<>(response, ErrorCode.UNAUTHORIZED.getHttpStatus());
    }

    /**
     * 处理JPA等持久化层抛出的“实体未找到”异常
     * 这是将基础设施层的异常转换为统一业务响应的好例子
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("[User-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), EntityNotFoundException.class.getSimpleName(), e.getMessage());
        ApiResponse<?> response = ApiResponse.error(ErrorCode.USER_NOT_FOUND);
        return new ResponseEntity<>(response, ErrorCode.USER_NOT_FOUND.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception e) {
        log.error("[User-Service] [{}]## Unexpected exception occured. message:{}", this.getClass().getSimpleName(), e.getMessage());
        ApiResponse<?> response = ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
