package com.ai.qa.service.application.service;

import com.ai.qa.service.application.dto.SaveHistoryCommand;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QARAG;
import com.ai.qa.service.domain.repo.QAHistoryRepository;
import com.ai.qa.service.infrastructure.feign.GeminiClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QAHistoryService {
    private final QAHistoryRepository repo;
    private final GeminiClient geminiClient;

    /**
     * 保存一条 QA 历史记录，同时生成 RAG 答案
     */
    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        // 1. 如果 answer 或 ragAnswer 为空，则调用 GeminiClient
        String answer = command.getAnswer();
        String ragAnswer = command.getRagAnswer();
        Long userId = command.getUserId();

        if (answer == null || answer.isEmpty()) {
            // 调用 Google Gemini 生成 AI 答案
            answer = geminiClient.askQuestion(userId, command.getQuestion());
        }

        QARAG rag = null;
        if (ragAnswer != null && !ragAnswer.isEmpty()) {
            rag = new QARAG("", ragAnswer);
        }

        // command.getUserid!=null
        // 先根据 command 构造领域模型
        QAHistory history = QAHistory.createNew(
                String.valueOf(command.getUserId()), // domain 用的是 String
                command.getQuestion(),
                answer,
                command.getSessionId(),
                rag // TODO: 如果需要 RAG，可以在这里接 Google AI 调用，生成 QARAG 对象
        );
        repo.save(history);
        return toDto(history);
    }

    /**
     * 查询用户的历史记录
     */
    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query) {
        // query.getUserId

        List<QAHistory> historyList = repo.findHistoryByUserId(String.valueOf(query.getUserId()));

        return historyList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 领域模型 -> DTO 转换
     */
    private QAHistoryDTO toDto(QAHistory history) {
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setSessionId(history.getSessionId());
        dto.setQuestion(history.getQuestion());
        dto.setAnswer(history.getAnswer());
        dto.setTimestamp(history.getTimestamp());

        // 如果存在 RAG 上下文，填充 ragAnswer
        if (history.getRag() != null) {
            dto.setRagAnswer(history.getRag().getGeneratedAnswer());
        }

        return dto;
    }
}