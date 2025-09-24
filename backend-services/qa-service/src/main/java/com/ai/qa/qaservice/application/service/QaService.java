package com.ai.qa.qaservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.qaservice.domain.entity.QaHistory;
import com.ai.qa.qaservice.domain.repo.QaHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QaService {
    private final GeminiClient geminiClient;
    private final QaHistoryRepository qaHistoryRepository;

    /**
     * 处理用户提问，调用Gemini API并保存历史记录
     * 
     * @param userId         用户ID
     * @param conversationId 对话ID
     * @param question       用户问题
     * @return AI回答
     */
    @Transactional
    public QaHistory processQuestion(Long userId, String conversationId, String question) {
        log.info("Processing question: {} for user: {}, conversation: {}", question, userId, conversationId);

        // 生成新对话ID（如果未提供）
        String actualConversationId = conversationId == null ? generateConversationId() : conversationId;

        // 获取当前对话的历史记录作为上下文
        List<QaHistory> conversationHistory = getConversationHistory(userId, actualConversationId);
        log.info("Conversation history size: {}", conversationHistory.size());

        // 构建包含上下文的提示词
        String prompt = buildPromptWithContext(question, conversationHistory);
        // log.info("Prompt: {}", prompt);

        // 调用Gemini API获取回答
        String answer = geminiClient.getGeminiResponse(prompt);
        // log.info("Gemini response: {}", answer);

        // 保存当前问答记录
        // 保存当前问答记录
        QaHistory history = QaHistory.builder()
                .userId(userId)
                .conversationId(actualConversationId)
                .question(question)
                .answer(answer)
                .createTime(LocalDateTime.now())
                .build();
        qaHistoryRepository.save(history);

        return history;
    }

    /**
     * 获取用户的所有对话ID列表
     */
    public List<String> getUserConversationsID(Long userId) {
        log.info("Getting all conversations for user: {}", userId);
        return qaHistoryRepository.findConversationIdsByUserId(userId);
    }

    /**
     * 获取指定对话的历史记录
     */
    public List<QaHistory> getConversationHistory(Long userId, String conversationId) {
        log.info("Getting history for user: {}, conversation: {}", userId, conversationId);
        return qaHistoryRepository.findByUserIdAndConversationIdOrderByCreateTimeAsc(userId, conversationId);
    }

    /**
     * 获取所有对话的历史记录
     */
    public List<QaHistory> getConversationAllHistory(Long userId) {
        log.info("Getting all history for user: {}", userId);
        return qaHistoryRepository.findByUserIdOrderByCreateTimeAsc(userId);
    }

    /**
     * 删除指定对话
     */
    @Transactional
    public int deleteConversation(Long userId, String conversationId) {
        log.info("Deleting conversation: {} for user: {}", conversationId, userId);
        return qaHistoryRepository.deleteByUserIdAndConversationId(userId, conversationId);
    }

    /**
     * 删除用户所有对话
     */
    @Transactional
    public int deleteAllConversations(Long userId) {
        log.info("Deleting all conversations for user: {}", userId);
        return qaHistoryRepository.deleteByUserId(userId);
    }

    /**
     * 生成新的对话ID（UUID + 毫秒数后三位）
     */
    private String generateConversationId() {
        // 获取当前时间毫秒数并取最后三位
        long currentTimeMillis = System.currentTimeMillis();
        int lastThreeDigits = (int) (currentTimeMillis % 1000);

        // 生成UUID并去除横线，然后拼接毫秒数后三位
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        uuid = uuid + String.format("%03d", lastThreeDigits);
        log.info("Generated new conversation ID: {}", uuid);

        return uuid;
    }

    /**
     * 构建包含上下文的提示词
     */
    private String buildPromptWithContext(String question, List<QaHistory> history) {
        log.info("Building prompt with context, history size: {}", history.size());

        StringBuilder promptBuilder = new StringBuilder();

        // 添加历史上下文
        if (!history.isEmpty()) {
            promptBuilder.append("以下是我们之前的对话历史，供你参考：\n");
            for (QaHistory h : history) {
                promptBuilder.append("用户：").append(h.getQuestion()).append("\n");
                promptBuilder.append("回答：").append(h.getAnswer()).append("\n\n");
            }
        }

        // 添加当前问题
        promptBuilder.append("现在，请回答用户的新问题：\n");
        promptBuilder.append(question);

        return promptBuilder.toString();
    }

}
