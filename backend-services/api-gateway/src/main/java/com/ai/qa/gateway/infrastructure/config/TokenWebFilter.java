package com.ai.qa.gateway.infrastructure.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.ai.qa.gateway.interfaces.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

/**
 * WebFilter that validates JWT and injects user context into Reactor Context.
 */
@Component
@Order(-1) // Ensure this filter runs early
public class TokenWebFilter implements WebFilter {

    public static final String TOKEN_KEY = "auth-token";
    public static final String USER_ID_KEY = "auth-user-id";

    private final JwtValidator jwtValidator;

    public TokenWebFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.defer(() -> {
            // Skip JWT validation for CORS preflight requests
            if ("OPTIONS".equals(exchange.getRequest().getMethod().name())) {
                return chain.filter(exchange);
            }

            String header = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                return chain.filter(exchange); // No token, continue
            }

            String token = header.substring(7);

            return Mono.fromCallable(() -> jwtValidator.validate(token))
                .flatMap(claims -> {
                    String subject = claims.getSubject();
                    Long userId = Long.parseLong(subject);
                    return chain.filter(exchange)
                                .contextWrite(ctx -> ctx.put(TOKEN_KEY, "Bearer " + token)
                                                        .put(USER_ID_KEY, userId));
                })
                .onErrorResume(ex -> {
                    String message;
                    if (ex instanceof ExpiredJwtException) {
                        message = "Token expired";
                    } else if (ex instanceof SignatureException) {
                        message = "Invalid token signature";
                    } else if (ex instanceof MalformedJwtException) {
                        message = "Malformed token";
                    } else if (ex instanceof UnsupportedJwtException) {
                        message = "Unsupported token format";
                    } else if (ex instanceof NumberFormatException) {
                        message = "Invalid subject format in token";
                    } else {
                        message = "JWT error: " + ex.getMessage();
                    }
                    // return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, message));
                    return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message);
                });
        });
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponseDTO errorBody = new ErrorResponseDTO(
            status.value(),
            status.getReasonPhrase(),
            message,
            exchange.getRequest().getPath().value(),
            exchange.getRequest().getId(),
            OffsetDateTime.now(ZoneOffset.UTC).toString()
        );

        try {
            byte[] bytes = new ObjectMapper().writeValueAsBytes(errorBody);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }



    /**
     * Utility method to retrieve current user ID from Reactor Context.
     */
    public static Mono<Long> currentUserId() {
        return Mono.deferContextual(ctxView ->
            Mono.justOrEmpty(ctxView.getOrEmpty(USER_ID_KEY))
                .cast(Long.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user"))));
    }
}
