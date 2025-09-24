package com.ai.qa.service.config;




import com.ai.qa.service.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Component
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> SKIP = List.of(
            "/api/user/login",
            "/api/user/register",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );
    //目标资源方法执行前执行。 返回true：放行    返回false：不放行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String originalUri = (String) request.getAttribute(
                org.springframework.web.util.WebUtils.ERROR_REQUEST_URI_ATTRIBUTE);
        log.warn("原始请求 URI: {}", originalUri);   // 真正被访问的路径
        log.warn("当前请求 URI: {}", request.getRequestURI()); // /error
        // ① 如果命中放行规则，直接过
        return true;
      /*  boolean skip = SKIP.stream()
                .anyMatch(p -> matcher.match(p, uri));
        if (skip) {
            return true;
        }

        //1,先获取请求头
        String token = request.getHeader("Authorization");
        String token1 = request.getHeader("authorization");
        String header = request.getHeader("content-type");

        response.setContentType("application/json;charset = UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        //2,判断请求头是否存在
        if (token == null || "".equals(token)){
            //请求头不存在或者请求头为空
            log.info("token不存在");

            //String result = mapper.writeValueAsString( new JwtResponse("Invalid token"));
            //response.getWriter().write(result);
            throw new RuntimeException("invalid token");
                }
        //3,请求头不正确
        Long userId =  null;
        try {
            Claims claims = jwtUtil.parseToken(token);
        }  catch (Exception e)  {
            log.info("请求头不正确!!");
            // Result error = Result.error(false, "NOT_LOGIN");
            //String result = mapper.writeValueAsString(new JwtResponse("Invalid token"));
            //response.getWriter().write(result);
            //throw new RuntimeException(ErrCode.getMessageByCode(ErrCode.INVALID_TOKEN));
            throw new RuntimeException("invalid token");
        }
        return true;*/
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle ... ");
    }

    //视图渲染完毕后执行，最后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion .... ");
    }
}