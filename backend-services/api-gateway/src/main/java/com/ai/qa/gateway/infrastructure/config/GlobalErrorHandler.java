package com.ai.qa.gateway.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import com.ai.qa.gateway.interfaces.dto.ErrorResponseDTO;

/**
 * Global error handler for WebFlux that returns structured JSON error responses.
 */
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Unexpected error";
        if (ex instanceof ResponseStatusException rse) {
            HttpStatusCode code = rse.getStatusCode();
            status = HttpStatus.valueOf(code.value()); // 显式转换
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        }

        String path = exchange.getRequest().getPath().value();
        String requestId = exchange.getRequest().getId(); // 可替换为 X-Request-ID header
        String timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString();

        ErrorResponseDTO errorBody = new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                requestId,
                timestamp
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorBody);
            exchange.getResponse().setStatusCode(status);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception serializationError) {
            return Mono.error(serializationError);
        }
    }
}
