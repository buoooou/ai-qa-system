package com.ai.qa.user.interceptor;

import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.common.JwtUtil;
import com.ai.qa.user.dto.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter  {

  /*  @Autowired
    private JwtUtil jwtUtil;

    private static final AntPathMatcher matcher = new AntPathMatcher();

    // 放行的 URI 列表
    private static final List<String> SKIP = List.of(
            "/api/user/login",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // ① 如果命中放行规则，直接过
        boolean skip = SKIP.stream()
                .anyMatch(p -> matcher.match(p, uri));
        if (skip) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        ObjectMapper mapper = new ObjectMapper();
        //2,判断请求头是否存在
        if (token == null || "".equals(token)){
            //请求头不存在或者请求头为空
            log.info("token不存在");
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.INVALID_TOKEN));

        }
        //3,请求头不正确
        Long userId =  null;
        try {
            Claims claims = jwtUtil.parseToken(token);
            String id = claims.getId();
        }  catch (Exception e)  {
            log.info("请求头不正确!!", e);
            // Result error = Result.error(false, "NOT_LOGIN");
            throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.INVALID_TOKEN));
        }
        log.info(">>> Filter: {} {}, response status={}",
                request.getMethod(), request.getRequestURI(),
                response.getStatus());
    }*/
}
