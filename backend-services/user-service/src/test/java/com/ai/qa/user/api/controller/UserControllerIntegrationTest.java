package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.LoginRequest;
import com.ai.qa.user.application.dto.Response;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;


import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testUser");
        request.setPassword("password123");

    Response<?> response = restTemplate.postForObject(
        "/api/users/auth/login",
        request,
        Response.class
    );

        assertEquals(200, response.getCode());
    }
}