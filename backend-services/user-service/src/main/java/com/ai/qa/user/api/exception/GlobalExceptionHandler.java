package com.ai.qa.user.api.exception;

import com.ai.qa.user.api.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<String> handleValidationException(ConstraintViolationException e) {
        return Response.error("参数校验失败: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<String> handleGeneralException(Exception e) {
        return Response.error("系统错误: " + e.getMessage());
    }
}