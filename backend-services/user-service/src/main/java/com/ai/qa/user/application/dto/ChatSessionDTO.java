package com.ai.qa.user.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ChatSessionDTO {
    private final String id;
    private final Long userId;
    private final String title;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
