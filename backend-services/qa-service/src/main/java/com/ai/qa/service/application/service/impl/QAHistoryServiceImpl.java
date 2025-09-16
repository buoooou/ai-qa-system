package com.ai.qa.service.application.service.impl;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.infrastructure.mapper.QAMapper;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QAHistoryServiceImpl implements QAHistoryService {

    private final QAHistoryRepo qaHistoryRepo;
    private final QAMapper qaMapper;

    @Override
    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        // 校验命令
        if (command.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        QAHistory history = qaMapper.toDomain(command);

        // 持久化
        qaHistoryRepo.save(history);

        // 转换为DTO
        return qaMapper.toDto(history);
    }

    @Override
    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query) {
        List<QAHistory> historyList;

        // 根据查询条件选择不同的查询方法
        if (query.getSessionId() != null) {
            historyList = qaHistoryRepo.findHistoryBySession(query.getSessionId());
        } else {
            historyList = qaHistoryRepo.findHistoryByUserId(query.getUserId());
        }

        return historyList.stream()
                .map(qaMapper::toDto)
                .collect(Collectors.toList());
    }
}
