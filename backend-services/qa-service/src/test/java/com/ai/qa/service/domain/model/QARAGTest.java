package com.ai.qa.service.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QARAG领域模型单元测试 - 简化版
 */
@DisplayName("QARAG领域模型测试")
class QARAGTest {

    @Test
    @DisplayName("预期能够创建RAG上下文")
    void shouldCreateRAGContext() {
        // Given
        String context = "这是检索到的相关信息";

        // When
        QARAG rag = QARAG.createContext(context);

        // Then
        assertNotNull(rag, "创建的RAG对象不应为空");
        assertEquals(context, rag.getContext(), "上下文内容应该匹配");
    }

    @Test
    @DisplayName("预期能够设置上下文内容")
    void shouldSetContext() {
        // Given
        QARAG rag = QARAG.createContext("初始上下文");
        String newContext = "更新后的上下文";

        // When
        rag.setContext(newContext);

        // Then
        assertEquals(newContext, rag.getContext(), "上下文应该被成功更新");
    }
}
