package com.ai.qa.user.api.exception;

import com.ai.qa.user.api.dto.Response;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 处理自定义业务异常 */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleRuntimeException(RuntimeException ex) {
        return Response.error(400, ex.getMessage());
    }

    /** 处理请求参数校验异常（@Valid、@NotBlank等） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        // 拼接所有字段错误信息
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Response.error(400, errorMsg);
    }

    /** 处理路径参数或请求参数校验异常（ConstraintViolation） */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return Response.error(400, errorMsg);
    }

    /** 处理其它未捕获异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<Void> handleException(Exception ex) {
        ex.printStackTrace(); // 可根据需要改成日志输出
        return Response.error(500, "服务器内部错误");
    }
}
