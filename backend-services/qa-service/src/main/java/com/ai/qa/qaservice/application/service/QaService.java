package com.ai.qa.qaservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.qaservice.domain.entity.QaHistory;
import com.ai.qa.qaservice.domain.repo.QaHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QaService {

    private final QaHistoryRepository qaHistoryRepository;
    private final GeminiClient geminiClient;

    /**
     * 处理用户提问，调用Gemini API并保存历史记录
     * 
     * @param userId   用户ID
     * @param question 用户问题
     * @return AI回答
     */
    @Transactional
    public QaHistory processQuestion(Long userId, String question) {
        log.info("Processing question: {} for user: {}", question, userId);

        // 获取最近5次的问答历史作为上下文
        List<QaHistory> recentHistory = getRecentHistory(userId, 5);
        log.info("Recent history.size(): {}", recentHistory.size());

        // 构建包含上下文的提示词
        String prompt = buildPromptWithContext(question, recentHistory);

        // 调用Gemini API获取回答
        String answer = geminiClient.getGeminiResponse(prompt);
        log.info("Gemini response: {}", answer);

        // 保存当前问答记录
        QaHistory history = new QaHistory();
        history.setUserId(userId);
        history.setQuestion(question);
        history.setAnswer(answer);
        history.setCreateTime(LocalDateTime.now());
        qaHistoryRepository.save(history);

        return history;
    }

    /**
     * 获取用户最近的问答历史
     * 
     * @param userId 用户ID
     * @param limit  最大记录数
     * @return 问答历史列表
     */
    public List<QaHistory> getRecentHistory(Long userId, int limit) {
        log.info("Getting recent history for user: {}, limit: {}", userId, limit);
        List<QaHistory> histories = qaHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);
        if (histories.size() > limit) {
            histories = histories.subList(0, limit);
        }
        return histories;
    }

    /**
     * 构建包含上下文的提示词
     * 
     * @param question 当前问题
     * @param history  历史问答记录
     * @return 构建好的提示词
     */
    private String buildPromptWithContext(String question, List<QaHistory> history) {
        log.info("Building prompt with context for question: {}, history.size(): {}", question, history.size());

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

    // 删除指定用户的问答历史
    @Transactional
    public int deleteHistory(Long userId) {
        log.info("Deleting history for user: {}", userId);
        return qaHistoryRepository.deleteByUserId(userId);
    }
}
