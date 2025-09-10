package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.request.UpdateNicknameRequest;
import com.ai.qa.user.api.dto.response.MessageResponse;
import com.ai.qa.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户信息修改接口")
@SecurityRequirement(name = "bearerAuth") // 要求携带JWT令牌
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/nickname")
    @Operation(summary = "修改昵称", description = "登录用户修改自己的昵称")
    public ResponseEntity<MessageResponse> updateNickname(@Valid @RequestBody UpdateNicknameRequest request) {
        // 从SecurityContext获取当前登录用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 调用服务修改昵称
        userService.updateNickname(username, request.getNickname());

        return ResponseEntity.ok(new MessageResponse("昵称修改成功"));
    }
}
