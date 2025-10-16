package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.Document;
import com.ai.qa.user.domain.model.DocumentId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository {

    Document save(Document document);

    List<Document> findByDocumentIdOrderByCreatedAtAsc(String documentId);

    Optional<Document> findTopByDocumentIdOrderByCreatedAtDesc(String documentId);

    List<Document> findByUserId(Long userId);

    void deleteByDocumentIdAndCreatedAtAfter(String documentId, LocalDateTime createdAfter);

    Optional<Document> findById(DocumentId id);
}

