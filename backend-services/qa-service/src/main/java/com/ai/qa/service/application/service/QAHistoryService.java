package com.ai.qa.service.application.service;

import com.ai.qa.service.application.dto.SaveHistoryCommand;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.service.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class QAHistoryService {
    private final QAHistoryMapper qaHistoryMapper;
    private final QAHistoryRepo qaHistoryRepo;

    public QAHistoryDTO saveHistory(SaveHistoryCommand command){
        String userId = command.getUserId();
        if (userId != null && userId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        QAHistory history = QAHistory.createNew(command.getUserId(), command.getAnswer(), command.getQuestion(), command.getSessionId());
        qaHistoryRepo.save(history);
        return qaHistoryMapper.toDto(history);
    }

    public List<QAHistoryDTO> queryUserHistoryByUserId(String userId){
        if (userId != null && userId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        List<QAHistory> qaHistoryList = qaHistoryRepo.findHistoryByUserId(userId);
        return qaHistoryMapper.toDtoList(qaHistoryList);
    }

    public List<QAHistoryDTO> queryUserHistoryBySessionId(String sessinId) {
        if (sessinId != null && sessinId.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        List<QAHistory> qaHistoryList = qaHistoryRepo.findHistoryByUserId(sessinId);
        return qaHistoryMapper.toDtoList(qaHistoryList);
    }
}