package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.CreateSessionRequest;
import com.ai.qa.service.application.dto.QAHistorySessionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class QAControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateSession() {
        CreateSessionRequest request = new CreateSessionRequest();
        request.setUserid("user123");

        ResponseEntity<QAHistorySessionDTO> response = restTemplate.postForEntity(
                "/api/qa/sessions",
                request,
                QAHistorySessionDTO.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getSessionId());
    }
}