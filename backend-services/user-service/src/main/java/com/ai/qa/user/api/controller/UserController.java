package com.ai.qa.user.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.response.LoginRsponse;
import com.ai.qa.user.api.dto.response.RegisterResponse;
import com.ai.qa.user.api.dto.response.Response;
import com.ai.qa.user.api.dto.response.UserInfo;
import com.ai.qa.user.application.service.UserCaseService;
import com.ai.qa.user.infrastructure.mapper.UserMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserCaseService userService;
    private final UserMapper mapper;

	@PostMapping("/login")
	@Operation(summary = "登录请求", description = "根据用户名密码验证登录")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "成功登录",
	                content = @Content(mediaType = "application/json",
	                        schema = @Schema(implementation = LoginRequest.class))),
	        @ApiResponse(responseCode = "401", description = "未授权"),
	        @ApiResponse(responseCode = "403", description = "密码错误"),
	        @ApiResponse(responseCode = "404", description = "用户不存在")
	})
    public ResponseEntity<Response<LoginRsponse>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(Response.success(userService.login(loginRequest.getUsername())));
    }

    @PostMapping("/register")
    @Operation(summary = "注册请求", description = "根据输入内容进行注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册登录",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = RegisterRequest.class)))
    })
    public ResponseEntity<Response<RegisterResponse>> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(Response.success(userService.register(mapper.toCommand(registerRequest))));
    }
    
    @GetMapping("/me")
    @Operation(summary = "用户查询请求", description = "根据token查询用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户查询",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Response<UserInfo>> getUser(HttpServletRequest request) {
    	String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7);
        return ResponseEntity.ok(Response.success(userService.getUserName(token)));
    }
}
