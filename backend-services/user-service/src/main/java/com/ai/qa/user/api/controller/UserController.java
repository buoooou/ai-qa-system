package com.ai.qa.user.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.ai.qa.user.application.dto.UpdateNicknameRequest;
import com.ai.qa.user.application.service.UserApplicationService;
import com.ai.qa.user.common.constants.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/***
 * 为什么user-service必须也要自己做安全限制？
 * 1。零信任网络 (Zero Trust Network)：在微服务架构中，你必须假设内部网络是不安全的。不能因为一个请求来自API Gateway就完全信任它。万一有其他内部服务被攻破，它可能会伪造请求直接调用user-service，绕过Gateway。如果user-service没有自己的安全防线，它就会被完全暴露。
 * 2。职责分离 (Separation of Concerns)：Gateway的核心职责是路由、限流、熔断和边缘认证。而user-service的核心职责是处理用户相关的业务逻辑，业务逻辑与谁能执行它是密不可分的。授权逻辑是业务逻辑的一部分，必须放在离业务最近的地方。
 * 3。细粒度授权 (Fine-Grained Authorization)：Gateway通常只做粗粒度的授权，比如“USER角色的用户可以访问/api/users/**这个路径”。但它无法知道更精细的业务规则，例如：
 * GET /api/users/{userId}: 用户123是否有权查看用户456的资料？
 * PUT /api/users/{userId}: 只有用户自己或者管理员才能修改用户信息。
 * 这些判断必须由user-service结合自身的业务逻辑和数据来完成。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    // private final UserService userService;
    // private final AuthService authService;
    private final UserApplicationService userApplicationService;

    @Operation(summary = "用户登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户登录成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "用户认证失败"),
        @ApiResponse(responseCode = "500", description = "预期外错误")
    })

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
        System.out.println("测试login");

        // try {
        //     AuthResponseDTO authResponseDto = authService.authenticate(request.getUsername(), request.getPassword());

        //     log.debug("[User-Service] [{}]## Token:{}, UserId:{}", this.getClass().getSimpleName(), authResponseDto.getToken(), authResponseDto.getUserId());

        //     return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.MSG_USER_LOGIN_SUCCESS, authResponseDto);
        // } catch (AuthenticationException e) {
        //     log.error("[User-Service] [{}]## Catched AuthenticationException. username:{}, password:{}", this.getClass().getSimpleName(), request.getUsername(), request.getPassword());
        //     return ApiResponseDTO.error(HttpStatus.UNAUTHORIZED.value(), Constants.MSG_USER_LOGIN_FAIL);
        // }
        return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.MSG_USER_LOGIN_SUCCESS, AuthResponseDTO.builder().token("token").build());
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
        System.out.println("测试register");

        // try {
        //     UserDTO userDTO = userService.register(request);
        //     AuthResponseDTO authResponseDto = AuthResponseDTO.builder().userId(userDTO.getId()).build();
        //     log.debug("[User-Service] [{}]## Token:{}, UserId:{}", this.getClass().getSimpleName(), authResponseDto.getToken(), authResponseDto.getUserId());

        //     return ApiResponseDTO.success(HttpStatus.CREATED.value(), Constants.MSG_USER_REGISTER_SUCCESS, authResponseDto);
        // } catch (UserServiceException e) {
        //     log.error("[User-Service] [{}]## {} code:{}, username:{}", this.getClass().getSimpleName(), Constants.MSG_USER_REGISTER_FAIL, e.getCode(), request.getUsername());
        //     return ApiResponseDTO.error(e.getCode(), Constants.MSG_USER_REGISTER_FAIL);
        // }
        return ApiResponseDTO.success(HttpStatus.CREATED.value(), Constants.MSG_USER_REGISTER_SUCCESS, AuthResponseDTO.builder().token("token").build());
    }

    @Operation(summary = "修改昵称")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户注册成功",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ApiResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "用户已存在"),
        @ApiResponse(responseCode = "500", description = "预期外错误")
    })
    @PutMapping("/{username}/nickname")
    public ApiResponseDTO<UserDTO> updateNickname(@PathVariable String username, @RequestBody UpdateNicknameRequest request) {
        log.debug("[User-Service] [{}]## {} Start.", this.getClass().getSimpleName(), "updateNickname");

        //校验。。。

        // 控制器只负责调用应用层，不处理业务逻辑
        // User updatedUser = userApplicationService.updateNickname(userId, request.getNickname());
        // // 为了安全，最佳实践是返回一个DTO而不是直接返回领域实体，这里为了简化直接返回
        // return ApiResponse.success(updatedUser);

        // try {
        //     UserDTO userDTO = userService.updateNickname(username, request.getNickname());
        //     log.debug("[User-Service] [{}]## username:{}, nickname:{}", this.getClass().getSimpleName(), username, request.getNickname());

        //     return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.MSG_UPDATED_NICKNAME_SUCCESS, userDTO);
        // } catch (UserServiceException e) {
        //     log.error("[User-Service] [{}]## {} code:{}, username:{}", this.getClass().getSimpleName(), Constants.MSG_UPDATED_NICKNAME_FAIL, e.getCode(), username);
        //     return ApiResponseDTO.error(e.getCode(), Constants.MSG_UPDATED_NICKNAME_FAIL);
        // }
        return ApiResponseDTO.success(HttpStatus.OK.value(), Constants.MSG_UPDATED_NICKNAME_SUCCESS, new UserDTO());
    }

    @GetMapping("/{userId}")
    public String getUserById(@PathVariable("userId") Long userId) {
        System.out.println("测试userid");
        return "userid:"+userId;
    }
}
