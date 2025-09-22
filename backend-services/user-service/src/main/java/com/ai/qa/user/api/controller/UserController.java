package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.request.UpdateNicknameRequest;
import com.ai.qa.user.api.dto.response.MessageResponse;
import com.ai.qa.user.api.dto.response.UserResponse;
import com.ai.qa.user.api.mapper.UserApiMapper;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.application.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户信息修改接口")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserApiMapper userApiMapper;

    @GetMapping("/{userId}")
    @Operation(summary = "查询用户信息", description = "根据用户ID查询用户详细信息")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {

        UserDTO userDTO = userService.getUserById(userId);
        UserResponse response = userApiMapper.toResponse(userDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/nickname")
    @Operation(summary = "修改昵称", description = "登录用户修改自己的昵称")
    public ResponseEntity<MessageResponse> updateNickname(@Valid @RequestBody UpdateNicknameRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        userService.updateNickname(username, request.getNickname());
        return ResponseEntity.ok(new MessageResponse("昵称修改成功"));
    }
}