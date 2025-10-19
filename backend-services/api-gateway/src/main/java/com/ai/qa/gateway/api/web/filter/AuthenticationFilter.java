package com.ai.qa.gateway.api.web.filter;

import lombok.extern.slf4j.Slf4j;

// @Component
//@RefreshScope // 为了动态刷新JWT密钥
@Slf4j
// public class AuthenticationFilter implements GlobalFilter, Ordered {
public class AuthenticationFilter {
    // @Value("${jwt.secret}")
    // private String jwtSecret;

    // @Override
    // public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    //     log.info("[API-Gateway] [{}]## Method {} start.", this.getClass().getSimpleName(), "filter");

    //     ServerHttpRequest request = exchange.getRequest();

    //     log.debug("[API-Gateway] [{}]## Request URI: {}", this.getClass().getSimpleName(), request.getURI());

    //     // 定义白名单路径，这些路径不需要JWT验证
    //     List<String> whiteList = List.of("/api/user/register", "/api/user/login", "/api/qa/ask");
    //     if (whiteList.contains(request.getURI().getPath())) {
    //         return chain.filter(exchange); // 放行
    //     }

    //     log.debug("[API-Gateway] [{}]## Request headers: {}", this.getClass().getSimpleName(), request.getHeaders());

    //     String authHeader = request.getHeaders().getFirst("Authorization");
    //     if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //         exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    //         return exchange.getResponse().setComplete();
    //     }

    //     String token = authHeader.substring(7);
    //     try {
    //         Claims claims = Jwts.parser()
    //                 .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
    //                 .build()
    //                 .parseSignedClaims(token)
    //                 .getPayload();

    //         // 验证通过，可以将用户信息放入请求头，传递给下游服务
    //         ServerHttpRequest mutatedRequest = request.mutate()
    //                 .header("X-User-Id", claims.getSubject())
    //                 .header("X-User-Name", claims.get("username", String.class))
    //                 .build();
    //         return chain.filter(exchange.mutate().request(mutatedRequest).build());
    //     } catch (Exception e) {
    //         exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    //         return exchange.getResponse().setComplete();
    //     }
    // }

    // @Override
    // public int getOrder() {
    //     // 鉴权过滤器应在日志过滤器之后，在路由之前，优先级要高
    //     return -100;
    // }
}