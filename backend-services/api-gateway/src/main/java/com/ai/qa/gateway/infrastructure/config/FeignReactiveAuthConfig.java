package com.ai.qa.gateway.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

@Configuration
public class FeignReactiveAuthConfig {

    @Bean
    public ReactiveHttpRequestInterceptor authRequestInterceptor() {
        return request ->
            Mono.deferContextual(ctxView -> {
                // 从 Reactor Context 取出 token
                if (ctxView.hasKey(TokenWebFilter.TOKEN_KEY)) {
                    String token = ctxView.get(TokenWebFilter.TOKEN_KEY);
                    request.headers().put("Authorization", java.util.List.of(token));
                }
                return Mono.just(request);
            });
    }
}
