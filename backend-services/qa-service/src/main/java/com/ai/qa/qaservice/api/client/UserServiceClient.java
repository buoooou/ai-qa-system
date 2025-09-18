package com.ai.qa.qaservice.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ai.qa.qaservice.api.dto.ApiResponse;

@FeignClient(name = "user-service-fm", path = "/api/user")
public interface UserServiceClient {
    @GetMapping("/getUserName")
    ResponseEntity<ApiResponse<String>> getUserName(@RequestParam("userId") Long userId);
}
