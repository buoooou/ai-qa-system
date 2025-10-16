package com.ai.qa.user.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.RegisterRequest;
import com.ai.qa.user.api.dto.UserResponse;
import com.ai.qa.user.api.exception.UserServiceException;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repository.UserRepo;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private User testUser;
    private LoginRequest authRequestDTO;
    private RegisterRequest registerRequest;

    @Mock
    private UserRepo userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        LocalDateTime currentTime = LocalDateTime.now();
        testUser = new User();
        testUser.setId(100L);
        testUser.setUsername("testUserXXX");
        testUser.setPassword("12345678");
        testUser.setNickname("NicoNico");
        testUser.setCreateTime(currentTime);
        testUser.setUpdateTime(currentTime);

        authRequestDTO = new LoginRequest();
        authRequestDTO.setUsername(testUser.getUsername());
        authRequestDTO.setPassword(testUser.getPassword());

        registerRequest = new RegisterRequest();
        registerRequest.setUsername(testUser.getUsername());
        registerRequest.setPassword(testUser.getPassword());
        registerRequest.setConfirmPassword(testUser.getPassword());
        registerRequest.setNickname(testUser.getNickname());
    }

    @Test
    void testGetUserByUsername_userexists() {
        given(userRepository.findByUsername("testUserXXX")).willReturn(Optional.of(testUser));

        UserResponse result = userService.getUserByUsername("testUserXXX");
        assertEquals("testUserXXX", result.getUsername());
        assertEquals("12345678", result.getPassword());
        assertEquals("NicoNico", result.getNickname());

        verify(userRepository, times(1)).findByUsername(any());
    }

    @Test
    void testGetUserByUsername_usernotexist() {
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.getUserByUsername(testUser.getUsername()));

        assertEquals("该用户不存在。", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testregister_userexists() {
        given(userRepository.existsByUsername("testUserXXX")).willReturn(true);

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.register(registerRequest));

        assertEquals("用户名已存在。", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername(any());
    }

    @Test
    void testregister_usernotexists() {
        given(userRepository.existsByUsername("testUserXXX")).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("aaBBccDDeeFF");

        UserResponse result = userService.register(registerRequest);
        assertEquals("testUserXXX", result.getUsername());
        assertEquals("aaBBccDDeeFF", result.getPassword());
        assertEquals("NicoNico", result.getNickname());

        verify(userRepository, times(1)).existsByUsername(any());
    }

    @Test
    void testUpdateNickname_userexists() {
        given(userRepository.findByUsername("testUserXXX")).willReturn(Optional.of(testUser));

        UserResponse result = userService.getUserByUsername("testUserXXX");
        assertEquals("testUserXXX", result.getUsername());
        assertEquals("12345678", result.getPassword());
        assertEquals("NicoNico", result.getNickname());

        verify(userRepository, times(1)).findByUsername(any());
    }

    @Test
    void testUpdateNickname_usernotexists() {
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());

        UserServiceException exception = assertThrows(UserServiceException.class, () -> userService.getUserByUsername(testUser.getUsername()));

        assertEquals("该用户不存在。", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
}
