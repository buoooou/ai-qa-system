package com.ai.qa.user.api.exception;

import com.ai.qa.user.api.dto.response.Response;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ai.qa.user.api.dto.response.MessageResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Response<Void> handleRuntimeException(RuntimeException e) {
        return Response.fail(500, e.getMessage());
    }

    @ExceptionHandler({
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            SignatureException.class,
            IllegalArgumentException.class
    })
    @ResponseBody
    public Response<Void> handleJwtExceptions(Exception ex) {
        String message;
        if (ex instanceof ExpiredJwtException) {
            message = "JWT令牌已过期";
        } else if (ex instanceof UnsupportedJwtException) {
            message = "不支持的JWT令牌";
        } else if (ex instanceof MalformedJwtException) {
            message = "无效的JWT令牌格式";
        } else if (ex instanceof SignatureException) {
            message = "JWT签名验证失败";
        } else if (ex instanceof IllegalArgumentException) {
            message = "JWT令牌为空或无效";
        } else {
            message = "JWT处理错误";
        }
        return Response.fail(HttpStatus.UNAUTHORIZED.value(), message);
    }
}