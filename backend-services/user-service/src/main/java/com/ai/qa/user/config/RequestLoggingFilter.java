package com.ai.qa.user.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 请求日志过滤器 - 记录所有HTTP请求的详细信息
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        
        // 记录请求开始信息
        logger.info("[REQUEST_START] {} {} - IP: {}, User-Agent: {}", 
                   request.getMethod(), request.getRequestURI(), 
                   request.getRemoteAddr(), request.getHeader("User-Agent"));
        
        // 记录所有请求头
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append("=").append(headerValue).append("; ");
        }
        logger.info("[REQUEST_HEADERS] {}", headers.toString());
        
        // 记录请求参数
        StringBuilder params = new StringBuilder();
        request.getParameterMap().forEach((key, values) -> {
            params.append(key).append("=").append(String.join(",", values)).append("; ");
        });
        if (params.length() > 0) {
            logger.info("[REQUEST_PARAMS] {}", params.toString());
        }
        
        try {
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
            
            // 记录响应信息
            long duration = System.currentTimeMillis() - startTime;
            logger.info("[REQUEST_END] {} {} - Status: {}, Duration: {}ms", 
                       request.getMethod(), request.getRequestURI(), 
                       response.getStatus(), duration);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("[REQUEST_ERROR] {} {} - 异常: {}, Duration: {}ms", 
                        request.getMethod(), request.getRequestURI(), 
                        e.getMessage(), duration, e);
            throw e;
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 可以在这里排除某些不需要记录的请求，比如静态资源
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") && !logger.isDebugEnabled();
    }
}