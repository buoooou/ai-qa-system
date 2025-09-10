package com.ai.qa.user.infrastructure.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ai.qa.user.common.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("[User-Service] [{}]## {} Start.", this.getClass().getSimpleName(), "doFilterInternal");

        final String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.debug("[User-Service] [{}]## The header does not include any information related authorization. header:{}", this.getClass().getSimpleName(), header);

            filterChain.doFilter(request, response);
            return;
        }
        final String token = header.substring(7);
        final String username = jwtUtil.extractUsername(token);
        log.debug("[User-Service] [{}]## Token:{}, extracted username:{}", this.getClass().getSimpleName(), token, username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("[User-Service] [{}]## Get userdetails. UserDetails.username:{}, UserDetails.password:{}", this.getClass().getSimpleName(), token, username);

            if (jwtUtil.validateToken(token, userDetails)) {
                // 创建认证令牌并设置到安全上下文
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                log.warn("[User-Service] [{}]## JWT令牌验证失败: {} - URI: {}", this.getClass().getSimpleName(), username, request.getRequestURI());
            }
        }
        filterChain.doFilter(request, response);
    }

}
