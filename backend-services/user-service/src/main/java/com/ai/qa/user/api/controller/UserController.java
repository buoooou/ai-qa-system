package com.ai.qa.user.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.request.LoginRequest;
import com.ai.qa.user.api.dto.request.RegisterRequest;
import com.ai.qa.user.api.dto.request.UpdateNickRequest;
import com.ai.qa.user.api.dto.response.Response;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserService;

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
    private final UserService userService;

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
	public ResponseEntity<Response<String>> login(@RequestBody LoginRequest loginRequest) {
		String token = userService.login(loginRequest);
		return ResponseEntity.ok(Response.success(token));
	}

    @PostMapping("/register")
    @Operation(summary = "注册请求", description = "根据输入内容进行注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册登录",
                    content = @Content(mediaType = "application/json", 
                            schema = @Schema(implementation = RegisterRequest.class)))
    })
    public ResponseEntity<Response<String>> register(@RequestBody RegisterRequest registerRequest) {
        boolean result = userService.register(registerRequest);
        if (result) {
            Response<String> message = Response.success(ErrCode.REGISTER_SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(400, ErrCode.REGISTER_FAILED));
    }

    @PostMapping("/update/{username}")
    @Operation(summary = "更新昵称请求", description = "根据用户名更改昵称")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新登录")
    })
    public ResponseEntity<Response<String>> updateNick(@PathVariable("username") String username,
             @RequestBody UpdateNickRequest updateNickRequest) {
        boolean result = userService.updateNick(username, updateNickRequest);
        if (result) {
            Response<String> message = Response.success(ErrCode.REGISTER_SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(400, ErrCode.UPDATE_NICK_FAILED));
    }   
}
