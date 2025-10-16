package com.ai.qa.gateway.interfaces.dto;

public record ErrorResponseDTO(
    int status,
    String error,
    String message,
    String path,
    String requestId,
    String timestamp
) {}
