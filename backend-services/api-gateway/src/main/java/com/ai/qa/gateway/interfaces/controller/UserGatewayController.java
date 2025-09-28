package com.ai.qa.gateway.interfaces.controller;

import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import com.ai.qa.gateway.interfaces.facade.AuthFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Gateway User", description = "User service proxy endpoints exposed by the gateway")
@RestController
@RequestMapping("/api/gateway/user")
@RequiredArgsConstructor
@Validated
public class UserGatewayController {

    private final AuthFacade authFacade;

    @Operation(summary = "Gateway user profile", description = "Delegates profile retrieval to user-service-fyb.")
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileGatewayResponse> profile(@PathVariable Long userId) {
        return ResponseEntity.ok(authFacade.profile(userId));
    }

    @Operation(summary = "Gateway update nickname", description = "Delegates nickname update to user-service-fyb.")
    @PostMapping("/{userId}/nickname")
    public ResponseEntity<UserProfileGatewayResponse> updateNickname(@PathVariable Long userId,
                                                                     @RequestBody @Validated UpdateNicknameGatewayRequest request) {
        return ResponseEntity.ok(authFacade.updateNickname(userId, request));
    }

    @Operation(summary = "Gateway chat sessions", description = "Delegates chat session listing to user-service-fyb.")
    @GetMapping("/{userId}/sessions")
    public ResponseEntity<List<ChatSessionResponseDTO>> sessions(@PathVariable Long userId) {
        return ResponseEntity.ok(authFacade.sessions(userId));
    }

    @Operation(summary = "Gateway create session", description = "Delegates session creation to user-service-fyb.")
    @PostMapping("/{userId}/sessions")
    public ResponseEntity<ChatSessionResponseDTO> createSession(@PathVariable Long userId,
                                                                @RequestBody @Validated CreateSessionGatewayRequest request) {
        return ResponseEntity.ok(authFacade.createSession(userId, request));
    }

    @Operation(summary = "Gateway get session", description = "Retrieves a specific chat session from user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}")
    public ResponseEntity<ChatSessionResponseDTO> getSession(@PathVariable Long userId,
                                                             @PathVariable Long sessionId) {
        return ResponseEntity.ok(authFacade.getSession(userId, sessionId));
    }

    @Operation(summary = "Gateway delete session", description = "Deletes a chat session using user-service-fyb.")
    @DeleteMapping("/{userId}/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long userId,
                                              @PathVariable Long sessionId) {
        authFacade.deleteSession(userId, sessionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Gateway chat history", description = "Delegates chat history retrieval to user-service-fyb.")
    @GetMapping("/{userId}/sessions/{sessionId}/history")
    public ResponseEntity<List<ChatHistoryResponseDTO>> history(@PathVariable Long userId,
                                                                @PathVariable Long sessionId,
                                                                @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(authFacade.history(userId, sessionId, limit));
    }
}
