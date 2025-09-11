package com.ai.qa.user.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("code", 400);
        response.put("message", "参数校验失败");
        response.put("errors", errors);
        response.put("timestamp", System.currentTimeMillis());
        
        log.warn("参数校验失败: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("code", 400);
        response.put("message", "参数绑定失败");
        response.put("errors", errors);
        response.put("timestamp", System.currentTimeMillis());
        
        log.warn("参数绑定失败: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }
        
        response.put("code", 400);
        response.put("message", "约束违反");
        response.put("errors", errors);
        response.put("timestamp", System.currentTimeMillis());
        
        log.warn("约束违反: {}", errors);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("code", 400);
        response.put("message", ex.getMessage());
        response.put("timestamp", System.currentTimeMillis());
        
        log.error("业务异常: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("code", 500);
        response.put("message", "系统内部错误");
        response.put("timestamp", System.currentTimeMillis());
        
        log.error("系统异常: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

