package com.ai.qa.service.api.controller;

import com.ai.qa.service.api.dto.CreateSessionRequest;
import com.ai.qa.service.application.dto.QAHistorySessionDTO;
import com.ai.qa.service.application.service.QAHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

class QAControllerTest {

    @Mock
    private QAHistoryService qaHistoryService;

    @InjectMocks
    private QAController qaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFeign() {
        when(qaHistoryService.testFeign()).thenReturn("Feign调用成功");
        String result = qaController.testFeign();
        assertEquals("Feign调用成功", result);
    }

    @Test
    void createSession_Success() {
        CreateSessionRequest request = new CreateSessionRequest();
        request.setUserId("user123");

        QAHistorySessionDTO sessionDTO = QAHistorySessionDTO.fromDomain("testUserId", "testSessionId", new ArrayList<>());
        sessionDTO.setSessionId("session123");

        when(qaHistoryService.initQAHistory(anyString())).thenReturn(sessionDTO);

        ResponseEntity<QAHistorySessionDTO> response = qaController.createSession(request);
        assertEquals("session123", response.getBody().getSessionId());
    }

    @Test
    void createSession_UserIdEmpty_ThrowsException() {
        CreateSessionRequest request = new CreateSessionRequest();
        request.setUserId("");

        assertThrows(RuntimeException.class, () -> qaController.createSession(request));
    }
}