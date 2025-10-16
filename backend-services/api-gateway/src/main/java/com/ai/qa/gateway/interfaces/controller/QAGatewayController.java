package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.infrastructure.config.TokenWebFilter;
import com.ai.qa.gateway.interfaces.dto.ChatRequestDTO;
import com.ai.qa.gateway.interfaces.facade.QAFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Gateway QA", description = "QA proxy endpoints exposed by the gateway")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/gateway/qa")
@RequiredArgsConstructor
@Slf4j
@Validated
public class QAGatewayController {

  private final QAFacade qaFacade;

  /**
   * 公共鉴权逻辑封装
   */
  private <T> Mono<ResponseEntity<T>> withAuthContext(Long requestUserId,
                                                      java.util.function.Function<Long, Mono<ResponseEntity<T>>> handler) {
      return Mono.deferContextual(ctx -> {
          Long tokenUserId = ctx.getOrDefault(TokenWebFilter.USER_ID_KEY, null);
          if (tokenUserId == null) {
              log.warn("Missing user context");
              return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
          }
          if (!tokenUserId.equals(requestUserId)) {
              log.warn("User mismatch: tokenUserId={}, requestUserId={}", tokenUserId, requestUserId);
              return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
          }
          return handler.apply(tokenUserId);
      });
  }

  @Operation(summary = "Proxy chat", description = "Delegates chat requests to qa-service-fyb.")
  @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Mono<ResponseEntity<Flux<String>>> chat(@RequestBody @Validated ChatRequestDTO request) {
      return withAuthContext(request.getUserId(), userId ->
              Mono.just(ResponseEntity.ok()
                      .contentType(MediaType.TEXT_EVENT_STREAM)
                      .body(qaFacade.chat(request))));
  }
}
