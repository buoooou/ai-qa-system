package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.api.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

        Response<?> response = restTemplate.postForObject(
                "/api/users/login",
                request,
                Response.class
        );

        assertEquals(200, response.getCode());
    }
}