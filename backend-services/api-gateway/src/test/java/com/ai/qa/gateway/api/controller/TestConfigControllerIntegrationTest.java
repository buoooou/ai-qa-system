package com.ai.qa.gateway.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class TestConfigControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testLogin() {
        String response = restTemplate.getForObject(
                "/api/test/config",
                String.class
        );

        assertTrue(response.contains("测试JWT"));
    }
}