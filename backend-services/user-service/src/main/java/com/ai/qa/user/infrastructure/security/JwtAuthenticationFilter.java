package com.ai.qa.user.infrastructure.security;

import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.infrastructure.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwtToken);
            } catch (Exception e) {
                log.warn("Unable to get JWT Token or JWT Token has expired");
                sendErrorResponse(response, ErrCode.TOKEN_INVALID);
                return;
            }
        } else if (StringUtils.hasText(requestTokenHeader)) {
            log.warn("JWT Token does not begin with Bearer String");
            sendErrorResponse(response, ErrCode.TOKEN_INVALID);
            return;
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // if token is valid configure Spring Security to manually set authentication
                if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    sendErrorResponse(response, ErrCode.TOKEN_EXPIRED);
                    return;
                }
            } catch (Exception e) {
                log.error("Authentication error: ", e);
                sendErrorResponse(response, ErrCode.UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrCode errCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", errCode.getCode());
        errorResponse.put("message", errCode.getMessage());
        errorResponse.put("data", null);
        
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 不需要JWT验证的路径
        return path.equals("/api/users/login") ||
               path.equals("/api/users/register") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v2/api-docs") ||
               path.startsWith("/swagger-resources/") ||
               path.equals("/v3/api-docs") ||
               path.startsWith("/webjars/");
    }
}