package com.ai.qa.gateway.interfaces.facade;

import com.ai.qa.gateway.infrastructure.feign.UserServiceClient;
import com.ai.qa.gateway.interfaces.dto.AuthRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final UserServiceClient userServiceClient;

    public AuthResponseDTO login(AuthRequestDTO request) {
        return userServiceClient.login(request);
    }

    public AuthResponseDTO register(AuthRequestDTO request) {
        return userServiceClient.register(request);
    }
}
