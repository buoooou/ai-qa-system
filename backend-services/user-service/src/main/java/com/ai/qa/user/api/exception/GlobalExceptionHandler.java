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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGlobalException(Exception e) {
        log.error("[User-Service] [{}]## {} occured. message:{}", this.getClass().getSimpleName(), Exception.class.getName(), e.getMessage());
        ApiResponseDTO<?> response = ApiResponseDTO.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.GLOBAL_ERROR_MSG);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
