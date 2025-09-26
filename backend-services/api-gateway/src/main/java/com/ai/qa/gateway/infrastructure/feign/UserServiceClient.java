package com.ai.qa.gateway.infrastructure.feign;

import com.ai.qa.gateway.interfaces.dto.AuthRequestDTO;
import com.ai.qa.gateway.interfaces.dto.AuthResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service-fyb", contextId = "gatewayUserClient")
public interface UserServiceClient {

    @PostMapping("/api/user/login")
    AuthResponseDTO login(@RequestBody AuthRequestDTO request);

    @PostMapping("/api/user/register")
    AuthResponseDTO register(@RequestBody AuthRequestDTO request);
}
