package com.ai.qa.user.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentId implements Serializable {

    @Column(name = "document_id", nullable = false, length = 36)
    private String documentId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

