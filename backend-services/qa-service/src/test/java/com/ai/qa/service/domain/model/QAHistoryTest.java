package com.ai.qa.service.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QAHistory领域模型单元测试 - 简化版
 */
@DisplayName("QAHistory领域模型测试")
class QAHistoryTest {

    @Test
    @DisplayName("预期能够创建新的问答历史记录")
    void shouldCreateNewQAHistory() {
        // Given
        String userId = "user123";
        String question = "1+1等于几";
        String answer = "1+1等于2";
        String sessionId = "session456";

        // When
        QAHistory history = QAHistory.createNew(userId, question, answer, sessionId);

        // Then
        assertNotNull(history, "创建的问答历史不应为空");
        assertEquals(userId, history.getUserId(), "用户ID应该匹配");
        assertEquals(question, history.getQuestion(), "问题应该匹配");
        assertEquals(answer, history.getAnswer(), "答案应该匹配");
        assertNotNull(history.getTimestamp(), "时间戳不应为空");
    }

    @Test
    @DisplayName("预期能够获取RAG增强的答案")
    void shouldGetRAGEnhancedAnswer() {
        // Given
        QAHistory history = QAHistory.createNew("user123", "什么是AI", "AI是人工智能", "session456");
        QARAG rag = QARAG.createContext("人工智能相关上下文", "知识库");
        history.setRAG(rag);

        // When
        String ragAnswer = history.getRAGAnswer();

        // Then
        assertNotNull(ragAnswer, "RAG答案不应为空");
        assertTrue(ragAnswer.contains("AI是人工智能"), "RAG答案应该包含基础答案");
    }
}
