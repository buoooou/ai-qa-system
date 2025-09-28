package com.ai.qa.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service-wss")
public interface UserClient {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息String
     *
     * 注意：
     * 1. @GetMapping 里的路径必须与 user-service 中 Controller 方法的完整路径匹配。
     * 2. 方法签名 (方法名、参数) 可以自定义，但 @PathVariable, @RequestParam 等注解必须和远程接口保持一致。
     */
    @GetMapping("/api/users/{userId}") // <-- 这个路径要和 user-service 的接口完全匹配
    String getUserById(@PathVariable Long userId);
}
