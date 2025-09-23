package com.ai.qa.service.application.service;

import com.ai.qa.service.api.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QAHistoryService {

    private final QAHistoryRepo qaHistoryRepo;

    public QAHistoryDTO saveHistory(SaveHistoryCommand command) {
        // 验证命令
        if (command.getUserId() == null || command.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 创建问答历史记录
        QAHistory history = QAHistory.createNew(
            command.getUserId(),
            command.getQuestion(),
            command.getAnswer(),
            command.getSessionId()
        );
        
        // 保存到仓库
        QAHistory savedHistory = qaHistoryRepo.save(history);
        
        // 转换为DTO返回
        return toDto(savedHistory);
    }

    public List<QAHistoryDTO> queryUserHistory(QAHistoryQuery query) {
        if (query.getUserId() == null || query.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 查询用户历史记录
        List<QAHistory> historyList = qaHistoryRepo.findHistoryByUserId(query.getUserId());
        
        // 转换为DTO列表返回
        return toDtoList(historyList);
    }
    
    /**
     * 将领域模型转换为DTO
     */
    private QAHistoryDTO toDto(QAHistory history) {
        QAHistoryDTO dto = new QAHistoryDTO();
        dto.setId(history.getId() != null ? history.getId().toString() : null);
        dto.setUserId(history.getUserId()); // 直接使用String类型
        dto.setQuestion(history.getQuestion());
        dto.setAnswer(history.getAnswer());
        dto.setTimestamp(history.getTimestamp());
        dto.setSessionId(history.getSessionId());
        return dto;
    }
    
    /**
     * 将领域模型列表转换为DTO列表
     */
    private List<QAHistoryDTO> toDtoList(List<QAHistory> historyList) {
        return historyList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
