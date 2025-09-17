package com.ai.qa.user.api.controller;

import com.ai.qa.user.TestApplicationConfig;
import com.ai.qa.user.api.dto.LoginResponse;
import com.ai.qa.user.api.dto.UserApiResponse;
import com.ai.qa.user.api.dto.UserLoginRequest;
import com.ai.qa.user.api.dto.UserRegisterRequest;
import com.ai.qa.user.api.dto.UpdateNickRequest;
import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
    classes = TestApplicationConfig.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("testuser");
        request.setPassword("TestPass123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userName").value("testuser"))
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void testLoginSuccess() throws Exception {
        // 先注册用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("TestPass123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 然后登录
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("TestPass123");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(86400L));
    }

    @Test
    void testUpdateNickSuccess() throws Exception {
        // 先注册并登录用户
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("TestPass123");

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 登录获取token
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("TestPass123");

        String responseString = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        UserApiResponse<LoginResponse> loginResult = objectMapper.readValue(responseString, 
                objectMapper.getTypeFactory().constructParametricType(UserApiResponse.class, LoginResponse.class));
        String token = loginResult.getData().getToken();
        Long userId = loginResult.getData().getUserId();

        // 更新昵称
        UpdateNickRequest updateRequest = new UpdateNickRequest();
        updateRequest.setUserId(userId);
        updateRequest.setNickName("NewNickname");

        mockMvc.perform(put("/api/user/update-nick")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value("昵称更新成功"));

        // 验证昵称已更新
        User user = userRepository.findByUserName("testuser").orElseThrow();
        assert user.getNickName().equals("NewNickname");
    }
}