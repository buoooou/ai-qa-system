package com.ai.qa.user.domain.repositories;

import com.ai.qa.user.domain.model.Suggestion;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository {

    Suggestion save(Suggestion suggestion);

    List<Suggestion> findByDocumentId(String documentId);

    List<Suggestion> findByDocumentIdAndCreatedAtAfter(String documentId, java.time.LocalDateTime createdAfter);

    Optional<Suggestion> findById(String id);

    void deleteById(String id);
}

