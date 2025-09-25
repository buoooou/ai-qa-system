package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.domain.model.UserDto;
import com.ai.qa.user.application.dto.Response;
import com.ai.qa.user.application.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

            // mock userService.login 返回有效 LoginDto
            com.ai.qa.user.application.dto.LoginDto loginDto = new com.ai.qa.user.application.dto.LoginDto();
            loginDto.setUserid("1");
            loginDto.setUsername("testUser");
            loginDto.setNickname("testNick");
            loginDto.setToken("mockToken");
            when(userService.login("testUser", "password123")).thenReturn(loginDto);

            Response<?> response = userController.login(request);
            assertEquals(200, response.getCode());
    }

    @Test
    void login_Failure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("wrongPassword");

        UserDto userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setPassword("encodedPassword");

        when(userService.findByUsername("testUser")).thenReturn(userDto);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        Response<?> response = userController.login(request);
        assertNotEquals(200, response.getCode());
    }
}