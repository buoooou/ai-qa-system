package com.ai.qa.user.api.exception;

import com.ai.qa.user.api.dto.UserResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理
 */
@RestControllerAdvice
        public class GlobalExceptionHandler {

            private static final Logger log = LoggerFactory.getLogger(com.ai.qa.user.api.exception.GlobalExceptionHandler.class);

            // 处理校验异常
            @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
            public ResponseEntity<UserResponseDto<Void>> handleValidationException(Exception e) {
                log.warn("请求参数校验失败: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(UserResponseDto.fail(ErrCode.INVALID_PARAMS));
    }

    // 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<UserResponseDto<Void>> handleBusinessException(BusinessException e) {
        log.warn("业务异常 [{}]: {}", e.getErrCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(UserResponseDto.fail(e.getErrCode()));
    }

    // 处理认证/授权异常
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<UserResponseDto<Void>> handleAuthException(AuthenticationException e) {
        log.warn("认证失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(UserResponseDto.fail(ErrCode.INVALID_CREDENTIALS));
    }

    // 处理未预期的系统异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<UserResponseDto<Void>> handleUnexpectedException(Exception e) {
        log.error("系统内部异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(UserResponseDto.fail(ErrCode.INTERNAL_ERROR));
    }
}