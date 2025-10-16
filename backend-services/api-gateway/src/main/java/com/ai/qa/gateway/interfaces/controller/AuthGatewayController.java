package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.dto.LoginGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.RegisterGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.common.ApiResponseDTO;
import com.ai.qa.gateway.interfaces.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Tag(name = "Gateway Auth", description = "Authentication proxy endpoints exposed by the gateway")
@RestController
@RequestMapping("/api/gateway/auth")
@RequiredArgsConstructor
public class AuthGatewayController {

    private final AuthFacade authFacade;

    @Operation(summary = "Gateway login", description = "Delegates login requests to user-service-fyb.")
    @PostMapping("/login")
    public Mono<ApiResponseDTO<AuthResponseDTO>> login(@RequestBody @Validated LoginGatewayRequestDTO request) {
        return authFacade.login(request)
                .map(ApiResponseDTO::success)
                .onErrorResume(ex -> Mono.just(transformError(ex)));
    }

    @Operation(summary = "Gateway register", description = "Delegates registration requests to user-service-fyb.")
    @PostMapping("/register")
    public Mono<ApiResponseDTO<AuthResponseDTO>> register(@RequestBody @Validated RegisterGatewayRequestDTO request) {
        return authFacade.register(request)
                .map(ApiResponseDTO::success)
                .onErrorResume(ex -> Mono.just(transformError(ex)));
    }

    private ApiResponseDTO<AuthResponseDTO> transformError(Throwable throwable) {
        if (throwable instanceof ResponseStatusException responseStatusException) {
            HttpStatus status = (HttpStatus) responseStatusException.getStatusCode();
            String message = responseStatusException.getReason();
            return ApiResponseDTO.failure(status.value(), message != null ? message : status.getReasonPhrase());
        }
        return ApiResponseDTO.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                throwable.getMessage() != null ? throwable.getMessage() : "Internal Server Error");
    }
}
