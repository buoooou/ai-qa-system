package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.UserApiResponse;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserRegisterRequest;
import com.ai.qa.user.api.dto.UpdateNickRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private com.ai.qa.user.application.UserApplicationService userApplicationService;

    @MockBean
    private com.ai.qa.user.application.query.UserQueryService userQueryService;

    @MockBean
    private com.ai.qa.user.security.JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // 设置JWT工具的模拟行为
        when(jwtUtil.generateToken(any(Long.class), any(String.class)))
                .thenReturn("test-jwt-token");
        when(jwtUtil.getUserIdFromToken(any(String.class)))
                .thenReturn(1L);
    }

    @Test
    void testRegisterSuccess() throws Exception {
        // 模拟用户实体
        com.ai.qa.user.domain.entity.User mockUser = new com.ai.qa.user.domain.entity.User();
        mockUser.setId(1L);
        mockUser.setUserName("testuser");
        
        // 模拟服务层行为
        when(userApplicationService.register(any(com.ai.qa.user.application.command.UserRegisterCommand.class)))
                .thenReturn(mockUser);

        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("TestPass123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("请求成功"))
                .andExpect(jsonPath("$.data.userName").value("testuser"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testLoginSuccess() throws Exception {
        // 模拟登录响应
        LoginResponse mockLoginResponse = new LoginResponse();
        mockLoginResponse.setUserId(1L);
        mockLoginResponse.setUsername("testuser");
        mockLoginResponse.setToken("test-jwt-token");
        mockLoginResponse.setTokenType("Bearer");
        mockLoginResponse.setExpiresIn(86400L);

        // 模拟服务层行为
        when(userApplicationService.login(any(com.ai.qa.user.application.command.UserLoginCommand.class)))
                .thenReturn(mockLoginResponse);

        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("testuser");
        request.setPassword("TestPass123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("请求成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400L));
    }

    @Test
    void testUpdateNickSuccess() throws Exception {
        // 模拟服务层行为 - void方法使用doNothing()
        org.mockito.Mockito.doNothing()
                .when(userApplicationService)
                .updateNickName(any(com.ai.qa.user.application.command.UserUpdateNickCommand.class), any(Long.class));

        UpdateNickRequest request = new UpdateNickRequest();
        request.setUserId(1L);
        request.setNickName("NewNickname");

        mockMvc.perform(put("/api/user/update-nick")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-jwt-token")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("请求成功"))
                .andExpect(jsonPath("$.data").value("昵称更新成功"));
    }
}