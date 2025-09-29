package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.ApiResponse;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.api.dto.CreateSessionRequest;
import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.application.dto.ChatMessageDTO;
import com.ai.qa.user.application.dto.ChatSessionDTO;
import com.ai.qa.user.application.dto.UpdateNicknameRequest;
import com.ai.qa.user.application.dto.UserProfileDTO;
import com.ai.qa.user.application.service.AuthApplicationService;
import com.ai.qa.user.application.service.UserApplicationService;
import com.ai.qa.user.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import com.ai.qa.user.common.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * REST endpoints providing user registration, authentication, profile, and chat history operations.
 */
@Tag(name = "User APIs", description = "User authentication, profile, and chat history endpoints")
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserApplicationService userApplicationService;
    private final AuthApplicationService authApplicationService;

    public UserController(UserApplicationService userApplicationService, AuthApplicationService authApplicationService) {
        this.userApplicationService = userApplicationService;
        this.authApplicationService = authApplicationService;
    }

    @Operation(summary = "Register new user", description = "Registers a new user account and returns a JWT token.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Validated RegisterRequest request) {
        AuthResponse response = authApplicationService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "User login", description = "Authenticates user credentials and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Validated LoginRequest request) {
        AuthResponse response = authApplicationService.login(request.getUsernameOrEmail(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    
    //@PreAuthorize("#userId.equals(authentication.principal)")
    // @PreAuthorize("hasRole('ADMIN')")
    // @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    // @PreAuthorize("true")
    @Operation(summary = "Get user profile", description = "Fetches profile information for the specified user.")
    @GetMapping("/{userId}/profile")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<UserProfileDTO>> profile(@Parameter(description = "ID of the user") @PathVariable Long userId) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Controller sees principal ID: " + principal.getId());
        
        UserProfileDTO profile = userApplicationService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @Operation(summary = "Update nickname", description = "Updates the nickname for the specified user.")
    @PostMapping("/{userId}/nickname")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<User>> updateNickname(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                             @RequestBody @Validated UpdateNicknameRequest request) {
        User updated = userApplicationService.updateNickname(userId, request.getNickname());
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "List chat sessions", description = "Returns chat sessions belonging to the specified user.")
    @GetMapping("/{userId}/sessions")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<List<ChatSessionDTO>>> listSessions(@Parameter(description = "ID of the user") @PathVariable Long userId) {
        List<ChatSessionDTO> sessions = userApplicationService.listSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @Operation(summary = "Create chat session", description = "Creates a new chat session for the specified user.")
    @PostMapping("/{userId}/sessions")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<ChatSessionDTO>> createSession(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                                     @RequestBody @Validated CreateSessionRequest request) {
        ChatSessionDTO session = userApplicationService.createSession(userId, request.getTitle());
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @Operation(summary = "Get chat session", description = "Retrieves a specific chat session for the user.")
    @GetMapping("/{userId}/sessions/{sessionId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<ChatSessionDTO>> getSession(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                                  @Parameter(description = "ID of the session") @PathVariable Long sessionId) {
        ChatSessionDTO session = userApplicationService.getSession(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(session));
    }

    @Operation(summary = "Delete chat session", description = "Deletes a chat session owned by the user.")
    @DeleteMapping("/{userId}/sessions/{sessionId}")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<Void>> deleteSession(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                           @Parameter(description = "ID of the session") @PathVariable Long sessionId) {
        userApplicationService.deleteSession(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "List chat history", description = "Retrieves chat history for a given session and user.")
    @GetMapping("/{userId}/sessions/{sessionId}/history")
    @PreAuthorize("#userId == principal.id")
    public ResponseEntity<ApiResponse<List<ChatMessageDTO>>> listHistory(@Parameter(description = "ID of the user") @PathVariable Long userId,
                                                                         @Parameter(description = "ID of the session") @PathVariable Long sessionId) {
        List<ChatMessageDTO> messages = userApplicationService.listHistoryBySession(userId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
}
