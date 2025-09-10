package com.ai.qa.user.api.exception;

import com.ai.qa.user.api.dto.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("Business exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Response.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<Void>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("Authentication exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Response.error(ErrCode.UNAUTHORIZED.getCode(), ErrCode.UNAUTHORIZED.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<Void>> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.warn("Bad credentials exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.ok(Response.error(ErrCode.INVALID_USERNAME_OR_PASSWORD.getCode(), ErrCode.INVALID_USERNAME_OR_PASSWORD.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.error(ErrCode.FORBIDDEN.getCode(), ErrCode.FORBIDDEN.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return ResponseEntity.badRequest()
                .body(Response.error(ErrCode.VALIDATION_ERROR.getCode(), errorMsg.toString()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleBindException(BindException e, HttpServletRequest request) {
        log.warn("Bind exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        return ResponseEntity.badRequest()
                .body(Response.error(ErrCode.VALIDATION_ERROR.getCode(), errorMsg.toString()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.warn("Constraint violation exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        
        StringBuilder errorMsg = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMsg.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
        }
        
        return ResponseEntity.badRequest()
                .body(Response.error(ErrCode.VALIDATION_ERROR.getCode(), errorMsg.toString()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.warn("Type mismatch exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        String errorMsg = String.format("参数 '%s' 类型错误，期望类型: %s", 
                e.getName(), e.getRequiredType().getSimpleName());
        return ResponseEntity.badRequest()
                .body(Response.error(ErrCode.PARAM_ERROR.getCode(), errorMsg));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("Method not supported exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Response.error(ErrCode.METHOD_NOT_ALLOWED.getCode(), ErrCode.METHOD_NOT_ALLOWED.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response<Void>> handleNotFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("Not found exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Response.error(ErrCode.NOT_FOUND.getCode(), ErrCode.NOT_FOUND.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Void>> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Illegal argument exception occurred at {}: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(Response.error(ErrCode.PARAM_ERROR.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception e, HttpServletRequest request) {
        log.error("Unexpected exception occurred at {}: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(ErrCode.SYSTEM_ERROR.getCode(), ErrCode.SYSTEM_ERROR.getMessage()));
    }
}