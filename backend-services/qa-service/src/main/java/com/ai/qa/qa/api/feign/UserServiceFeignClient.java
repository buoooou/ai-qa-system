package com.ai.qa.qa.api.feign;

import com.ai.qa.qa.api.dto.Response;
import com.ai.qa.qa.api.feign.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceFeignClient {
    
    /**
     * 根据用户ID获取用户信息
     * 使用了FeignClient注解,这个服务主要作用是在nacos注册中心中查找对应的服务，并调用服务提供的接口
     * @param userId 用户ID
     * @return 用户信息响应对象
     */
    @GetMapping("/api/users/getUserById")
    Response<User> getUserById(@RequestParam("userId") Long userId);
}