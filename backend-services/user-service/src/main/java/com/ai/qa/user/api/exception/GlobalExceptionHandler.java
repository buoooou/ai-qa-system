package com.ai.qa.user.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ai.qa.user.api.dto.ApiResponseDTO;
import com.ai.qa.user.common.constants.Constants;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(UserServiceException e) {
        log.error("[User-Service] [{}]## {} occured. HttpStatus:{}, message:{}", this.getClass().getSimpleName(), UserServiceException.class.getName(), e.getCode(), e.getMessage());
        // ErrorCode errorCode = ex.getErrorCode();
        // return new ResponseEntity<>(ApiResponse.failure(errorCode), errorCode.getHttpStatus());
        ApiResponseDTO<?> response = ApiResponseDTO.error(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getCode()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(IllegalStateException e) {
        log.error("[User-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), IllegalStateException.class.getName(), e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleBusinessException(AuthenticationException e) {
        log.error("[User-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), AuthenticationException.class.getName(), e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 处理JPA等持久化层抛出的“实体未找到”异常
     * 这是将基础设施层的异常转换为统一业务响应的好例子
     */
    // @ExceptionHandler(EntityNotFoundException.class)
    // public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
    //     log.warn("实体未找到: {}", ex.getMessage());
    //     ErrorCode errorCode = ErrorCode.USER_NOT_FOUND; // 映射到一个具体的业务错误码
    //     return new ResponseEntity<>(ApiResponse.failure(errorCode, ex.getMessage()), errorCode.getHttpStatus());
    // }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(Exception e) {
        log.error("[User-Service] [{}]## Unexpected exception occured. message:{}", this.getClass().getSimpleName(), e.getMessage());
        // ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        // return new ResponseEntity<>(ApiResponse.failure(errorCode), errorCode.getHttpStatus());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.GLOBAL_ERROR_MSG);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
