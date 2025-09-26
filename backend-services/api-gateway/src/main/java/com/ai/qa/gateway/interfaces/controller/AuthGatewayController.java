package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.interfaces.dto.AuthRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Gateway Auth", description = "Authentication proxy endpoints exposed by the gateway")
@RestController
@RequestMapping("/api/gateway/auth")
@RequiredArgsConstructor
public class AuthGatewayController {

    private final AuthFacade authFacade;

    @Operation(summary = "Gateway login", description = "Delegates login requests to user-service-fyb.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Validated AuthRequestDTO request) {
        return ResponseEntity.ok(authFacade.login(request));
    }

    @Operation(summary = "Gateway register", description = "Delegates registration requests to user-service-fyb.")
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Validated AuthRequestDTO request) {
        return ResponseEntity.ok(authFacade.register(request));
    }
}
