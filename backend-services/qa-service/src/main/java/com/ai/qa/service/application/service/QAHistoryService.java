package com.ai.qa.service.application.service;

import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QAHistoryService {

    @Autowired
    private QAHistoryRepo repo;

    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        QAHistory history = QAHistory.createNew(command.getUserId(), command.getQuestion(), command.getAnswer());
        repo.save(history);
        return toDto(history);
    }

    public List<QAHistoryDTO> queryUserHistory(Long userId) {
        List<QAHistory> historyList = repo.findHistoryBySession(String.valueOf(userId)); // 视为 sessionId
        return historyList.stream().map(this::toDto).collect(Collectors.toList());
    }

    private QAHistoryDTO toDto(QAHistory history) {
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setId(history.getId());
        dto.setUserId(history.getUserId());
        dto.setQuestion(history.getQuestion());
        dto.setAnswer(history.getAnswer());
        dto.setCreateTime(history.getCreateTime());
        return dto;
    }
}