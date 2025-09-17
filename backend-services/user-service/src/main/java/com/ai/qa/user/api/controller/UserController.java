package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.*;
import com.ai.qa.user.application.UserApplicationService;
import com.ai.qa.user.application.command.UserLoginCommand;
import com.ai.qa.user.application.command.UserRegisterCommand;
import com.ai.qa.user.application.command.UserUpdateNickCommand;
import com.ai.qa.user.application.query.UserQuery;
import com.ai.qa.user.application.query.UserQueryService;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final UserQueryService userQueryService;
    private final JwtUtil jwtUtil;

    public UserController(UserApplicationService userApplicationService, 
                         UserQueryService userQueryService,
                         JwtUtil jwtUtil) {
        this.userApplicationService = userApplicationService;
        this.userQueryService = userQueryService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "注册失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<User>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterCommand command = new UserRegisterCommand(request.getUsername(), request.getPassword());
        User user = userApplicationService.register(command);
        return ResponseEntity.ok(UserApiResponse.success(user));
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取JWT Token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "登录失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<LoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginCommand command = new UserLoginCommand(request.getUsername(), request.getPassword());
        LoginResponse loginResponse = userApplicationService.login(command);
        return ResponseEntity.ok(UserApiResponse.success(loginResponse));
    }

    /**
     * 更新用户昵称
     */
    @PutMapping("/update-nick")
    @Operation(summary = "更新用户昵称", description = "更新当前登录用户的昵称")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "更新失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<String>> updateNick(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateNickRequest request) {
        // 从Authorization头中提取token并验证
        String token = authorization.replace("Bearer ", "");
        Long currentUserId = jwtUtil.getUserIdFromToken(token);

        UserUpdateNickCommand command = new UserUpdateNickCommand(request.getUserId(), request.getNickName());
        userApplicationService.updateNickName(command, currentUserId);
        return ResponseEntity.ok(UserApiResponse.success("昵称更新成功"));
    }

    /**
     * 生成用户向量嵌入
     */
    @PostMapping("/generate-vector")
    @Operation(summary = "生成用户向量嵌入", description = "为用户生成向量嵌入用于相似性搜索")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "生成成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "生成失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<String>> generateVector(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long userId) {
        // 从Authorization头中提取token并验证
        String token = authorization.replace("Bearer ", "");
        Long currentUserId = jwtUtil.getUserIdFromToken(token);

        // 验证用户只能生成自己的向量
        if (!userId.equals(currentUserId)) {
            throw new IllegalArgumentException("只能生成自己的向量嵌入");
        }

        userApplicationService.generateUserVectorEmbedding(userId);
        return ResponseEntity.ok(UserApiResponse.success("向量嵌入生成成功"));
    }

    /**
     * 查找相似用户
     */
    @GetMapping("/similar")
    @Operation(summary = "查找相似用户", description = "基于向量相似性查找相似用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "查询失败", content = @Content(schema = @Schema(implementation = UserApiResponse.class)))
    })
    public ResponseEntity<UserApiResponse<List<User>>> findSimilarUsers(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0.5") Double threshold) {
        // 从Authorization头中提取token并验证
        String token = authorization.replace("Bearer ", "");
        Long currentUserId = jwtUtil.getUserIdFromToken(token);

        // 验证用户只能查询自己的相似用户
        if (!userId.equals(currentUserId)) {
            throw new IllegalArgumentException("只能查询自己的相似用户");
        }

        UserQuery query = new UserQuery(userId, null, limit, threshold);
        List<User> similarUsers = userQueryService.findSimilarUsers(query);
        return ResponseEntity.ok(UserApiResponse.success(similarUsers));
    }
}
