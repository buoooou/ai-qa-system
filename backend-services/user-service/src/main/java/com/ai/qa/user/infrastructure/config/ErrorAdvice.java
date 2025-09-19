package com.ai.qa.user.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorAdvice {
    @ExceptionHandler(NoHandlerFoundException.class)
    public void log404(NoHandlerFoundException ex) {
        log.warn("下游 404: {}", ex.getRequestURL());
    }
}
