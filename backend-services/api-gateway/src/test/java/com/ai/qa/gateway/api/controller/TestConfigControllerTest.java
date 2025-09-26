package com.ai.qa.gateway.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TestConfigControllerTest {

    @InjectMocks
    private TestConfigController testConfigController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(testConfigController, "jwtSecret", "testSecret");
    }

    @Test
    void login() {
        String result = testConfigController.login();
        assertEquals("测试JWT:testSecret", result);
    }
}