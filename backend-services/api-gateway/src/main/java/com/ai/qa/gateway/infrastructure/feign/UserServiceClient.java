package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.LoginGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatHistoryResponseDTO;
import com.ai.qa.gateway.interfaces.dto.ChatSessionResponseDTO;
import com.ai.qa.gateway.interfaces.dto.CreateSessionGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.RegisterGatewayRequestDTO;
import com.ai.qa.gateway.interfaces.dto.UpdateNicknameGatewayRequest;
import com.ai.qa.gateway.interfaces.dto.UserProfileGatewayResponse;
import com.ai.qa.gateway.interfaces.dto.UserServiceApiResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service-fyb", contextId = "gatewayUserClient")
public interface UserServiceClient {

    @PostMapping("/api/user/login")
    UserServiceApiResponseDTO<AuthResponseDTO> login(@RequestBody LoginGatewayRequestDTO request);

    @PostMapping("/api/user/register")
    UserServiceApiResponseDTO<AuthResponseDTO> register(@RequestBody RegisterGatewayRequestDTO request);

    @GetMapping("/api/user/{userId}/profile")
    UserServiceApiResponseDTO<UserProfileGatewayResponse> profile(@PathVariable("userId") Long userId);

    @PostMapping("/api/user/{userId}/nickname")
    UserServiceApiResponseDTO<UserProfileGatewayResponse> updateNickname(@PathVariable("userId") Long userId,
                                                                         @RequestBody UpdateNicknameGatewayRequest request);

    @GetMapping("/api/user/{userId}/sessions")
    UserServiceApiResponseDTO<List<ChatSessionResponseDTO>> sessions(@PathVariable("userId") Long userId);

    @PostMapping("/api/user/{userId}/sessions")
    UserServiceApiResponseDTO<ChatSessionResponseDTO> createSession(@PathVariable("userId") Long userId,
                                                                    @RequestBody CreateSessionGatewayRequest request);

    @GetMapping("/api/user/{userId}/sessions/{sessionId}")
    UserServiceApiResponseDTO<ChatSessionResponseDTO> getSession(@PathVariable("userId") Long userId,
                                                                 @PathVariable("sessionId") Long sessionId);

    @DeleteMapping("/api/user/{userId}/sessions/{sessionId}")
    UserServiceApiResponseDTO<Void> deleteSession(@PathVariable("userId") Long userId,
                                                  @PathVariable("sessionId") Long sessionId);

    @GetMapping("/api/user/{userId}/sessions/{sessionId}/history")
    UserServiceApiResponseDTO<List<ChatHistoryResponseDTO>> history(@PathVariable("userId") Long userId,
                                                                    @PathVariable("sessionId") Long sessionId,
                                                                    @RequestParam(value = "limit", required = false) Integer limit);

    @GetMapping("/api/user/sessions/{sessionId}/ownership")
    UserServiceApiResponseDTO<Boolean> isSessionOwnedBy(@PathVariable("sessionId") Long sessionId,
                                                        @RequestParam("userId") Long userId);
}
