package com.ai.qa.service.domain;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QARAG;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DDD领域层单元测试示例
 * 
 * 测试重点：
 * 1. 业务规则验证
 * 2. 领域模型行为
 * 3. 不依赖外部技术框架
 */
@DisplayName("QAHistory 领域模型测试")
public class QAHistoryTest {

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
        assertNotNull(history.getId(), "ID应该自动生成");
        assertNotNull(history.getTimestamp(), "时间戳应该自动设置");
        assertEquals(userId, history.getUserId(), "用户ID应该正确设置");
        assertEquals(question, history.getQuestion(), "问题应该正确设置");
        assertEquals(answer, history.getAnswer(), "答案应该正确设置");
        assertEquals(sessionId, history.getSessionId(), "会话ID应该正确设置");
    }

    @Test
    @DisplayName("应该能够获取RAG增强答案")
    void shouldGetRAGEnhancedAnswer() {
        // Given
        QAHistory history = QAHistory.createNew("user123", "问题", "原始答案", "session");
        QARAG rag = QARAG.createContext("这是额外的上下文信息", "知识库");
        history.setRAG(rag);
        
        // When
        String enhancedAnswer = history.getRAGAnswer();
        
        // Then
        assertNotNull(enhancedAnswer, "增强答案不应为空");
        assertTrue(enhancedAnswer.contains("原始答案"), "应该包含原始答案");
        assertTrue(enhancedAnswer.contains("这是额外的上下文信息"), "应该包含RAG上下文");
    }
}
