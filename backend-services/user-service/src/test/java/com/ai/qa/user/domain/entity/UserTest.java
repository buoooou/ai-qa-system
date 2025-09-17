package com.ai.qa.user.domain.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User领域实体单元测试 - 简化版
 */
@DisplayName("User领域实体测试")
class UserTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("预期能够创建新用户")
    void shouldCreateNewUser() {
        // Given
        String id = "user123";
        String username = "testuser";
        String password = "password123";

        // When
        User user = User.createNewUser(id, username, password, passwordEncoder);

        // Then
        assertNotNull(user, "创建的用户不应为空");
        assertEquals(id, user.getId(), "用户ID应该匹配");
        assertEquals(username, user.getUsername(), "用户名应该匹配");
        assertTrue(passwordEncoder.matches(password, user.getPassword()), "密码应该能够验证");
        assertEquals(User.UserStatus.ACTIVE, user.getStatus(), "新用户状态应该是ACTIVE");
    }

    @Test
    @DisplayName("预期能够验证密码")
    void shouldVerifyPassword() {
        // Given
        String password = "password123";
        User user = User.createNewUser("user123", "testuser", password, passwordEncoder);

        // When & Then
        assertTrue(user.verifyPassword(password, passwordEncoder), "正确密码应该验证通过");
        assertFalse(user.verifyPassword("wrongpassword", passwordEncoder), "错误密码应该验证失败");
    }
}
