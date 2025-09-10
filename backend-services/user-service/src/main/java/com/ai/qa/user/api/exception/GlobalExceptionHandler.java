package com.ai.qa.user.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常统一处理
 * 返回标准化错误响应
 *
 * @author Chen Guoping
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex,
                                                                        HttpServletRequest request) {
        log.warn("业务异常 => code:{}, msg:{}, path:{}", ex.getErrorCode(), ex.getErrorMessage(), request.getRequestURI());
        return ResponseEntity.status(determineHttpStatus(ex.getErrorCode()))
                             .body(buildError(ex.getErrorCode(), ex.getErrorMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidException(MethodArgumentNotValidException ex,
                                                                     HttpServletRequest request) {
        log.warn("参数校验异常 => {}, path:{}", ex.getMessage(), request.getRequestURI());
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err ->
                details.put(((FieldError) err).getField(), err.getDefaultMessage()));
        Map<String, Object> error = buildError(ErrCode.BAD_REQUEST, ErrCode.MSG_BAD_REQUEST);
        error.put("details", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(buildError(ErrCode.USER_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobal(Exception ex, HttpServletRequest request) {
        log.error("系统异常 => {}, path:{}", ex.getMessage(), request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(buildError(ErrCode.INTERNAL_SERVER_ERROR, ErrCode.MSG_INTERNAL_ERROR));
    }

    /* ---------------- 工具方法 ---------------- */

    private Map<String, Object> buildError(Integer code, String msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("errorCode", code);
        map.put("errorMessage", msg);
        map.put("timestamp", System.currentTimeMillis());
        return map;
    }

    private HttpStatus determineHttpStatus(Integer errorCode) {
        return switch (errorCode) {
            case ErrCode.UNAUTHORIZED       -> HttpStatus.UNAUTHORIZED;
            case ErrCode.FORBIDDEN          -> HttpStatus.FORBIDDEN;
            case ErrCode.NOT_FOUND          -> HttpStatus.NOT_FOUND;
            case ErrCode.CONFLICT           -> HttpStatus.CONFLICT;
            case ErrCode.INTERNAL_SERVER_ERROR,
                 ErrCode.SERVICE_UNAVAILABLE -> HttpStatus.INTERNAL_SERVER_ERROR;
            default                         -> HttpStatus.BAD_REQUEST;
        };
    }
}