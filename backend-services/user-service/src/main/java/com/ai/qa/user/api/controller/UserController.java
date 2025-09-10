package com.ai.qa.user.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.ApiResponseDTO;
import com.ai.qa.user.api.dto.AuthRequestDTO;
import com.ai.qa.user.api.dto.AuthResponseDTO;
import com.ai.qa.user.api.dto.UserDTO;
import com.ai.qa.user.api.exception.UserServiceException;
import com.ai.qa.user.application.service.AuthService;
import com.ai.qa.user.application.service.UserService;
import com.ai.qa.user.common.constants.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "用户登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户登录成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "用户认证失败"),
        @ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PostMapping("/login")
    public ApiResponseDTO<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        log.debug("[User-Service] [{}]## {} Start.", this.getClass().getSimpleName(), "login");

        try {
            AuthResponseDTO authResponseDto = authService.authenticate(request.getUsername(), request.getPassword());

            log.debug("[User-Service] [{}]## Token:{}, UserId:{}", this.getClass().getSimpleName(), authResponseDto.getToken(), authResponseDto.getUserId());

            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.MSG_USER_LOGIN_SUCCESS, authResponseDto);
        } catch (AuthenticationException e) {
            log.error("[User-Service] [{}]## Catched AuthenticationException. username:{}, password:{}", this.getClass().getSimpleName(), request.getUsername(), request.getPassword());
            return ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_LOGIN_FAIL);
        }
    }

    @Operation(summary = "用户注册")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户注册成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "用户已存在"),
        @ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PostMapping("/register")
    public ApiResponseDTO<AuthResponseDTO> register(@RequestBody AuthRequestDTO request) {
        log.debug("[User-Service] [{}]## {} Start.", this.getClass().getSimpleName(), "register");

        try {
            UserDTO userDTO = userService.register(request);
            AuthResponseDTO authResponseDto = AuthResponseDTO.builder().userId(userDTO.getId()).build();
            log.debug("[User-Service] [{}]## Token:{}, UserId:{}", this.getClass().getSimpleName(), authResponseDto.getToken(), authResponseDto.getUserId());

            return ApiResponseDTO.success(HttpStatus.CREATED.value(), Constants.MSG_USER_REGISTER_SUCCESS, authResponseDto);
        } catch (UserServiceException e) {
            log.error("[User-Service] [{}]## {} code:{}, username:{}", this.getClass().getSimpleName(), Constants.MSG_USER_REGISTER_FAIL, e.getCode(), request.getUsername());
            return ApiResponseDTO.error(e.getCode(), Constants.MSG_USER_REGISTER_FAIL);
        }
    }

    @Operation(summary = "修改昵称")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户注册成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "用户已存在"),
        @ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PutMapping("/{username}")
    public ApiResponseDTO<UserDTO> updateNickname(@PathVariable String username, @RequestBody AuthRequestDTO request) {
        log.debug("[User-Service] [{}]## {} Start.", this.getClass().getSimpleName(), "updateNickname");

        try {
            UserDTO userDTO = userService.updateNickname(username, request.getNickname());
            log.debug("[User-Service] [{}]## username:{}, nickname:{}", this.getClass().getSimpleName(), username, request.getNickname());

            return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.MSG_UPDATED_NICKNAME_SUCCESS, userDTO);
        } catch (UserServiceException e) {
            log.error("[User-Service] [{}]## {} code:{}, username:{}", this.getClass().getSimpleName(), Constants.MSG_UPDATED_NICKNAME_FAIL, e.getCode(), username);
            return ApiResponseDTO.error(e.getCode(), Constants.MSG_UPDATED_NICKNAME_FAIL);
        }
    }
}
