package com.ai.qa.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * User Service Feign客户端
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/user/{userId}")
    String getUserById(@PathVariable("userId") Long userId);
}