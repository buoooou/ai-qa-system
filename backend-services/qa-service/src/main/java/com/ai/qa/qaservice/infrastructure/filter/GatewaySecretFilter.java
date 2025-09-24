package com.ai.qa.qaservice.infrastructure.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GatewaySecretFilter extends OncePerRequestFilter {
    @Value("${gateway.secretId}")
    private String gatewaySecret;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // OpenDoc、Actuator等路径的场合，直接放行
        if (path.equals("/swagger-ui.html") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                // Actuator健康检查相关路径
                path.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 其他的验证网关的路径进行校验
        String secret = request.getHeader("X-Gateway-Secret");

        if (secret == null || !secret.equals(gatewaySecret)) {
            log.warn("网关密钥验证失败，路径: {}", path);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"请通过网关访问\"}");

            return; // 直接返回错误，不继续执行
        }

        log.info("网关密钥验证成功，路径: {}", path);

        filterChain.doFilter(request, response); // 验证通过，继续执行
    }
}