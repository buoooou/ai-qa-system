package com.ai.qa.service.application.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ai.qa.service.api.dto.QAHistoryDto;
import com.ai.qa.service.application.dto.QAHistoryQuery;
import com.ai.qa.service.application.dto.SaveHistoryCommand;
import com.ai.qa.service.application.service.QAHistoryService;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repo.QAHistoryRepo;
import com.ai.qa.service.infrastructure.feign.GeminiClient;
import com.ai.qa.service.infrastructure.persistence.mappers.QAHistoryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j                      
@Service                    
@RequiredArgsConstructor    
public class QAHistoryServiceImpl implements QAHistoryService  {

    private final QAHistoryRepo qaHistoryRepo;
    private final QAHistoryMapper qaMapper;
    
    /**
     * Gemini AI客户端
     */
    private final GeminiClient geminiClient;
    
    //保存问答
    public QAHistoryDto saveHistory(SaveHistoryCommand command){
        log.info("开始处理问答请求，用户ID: {}, 问题: {}", 
        		command.getUserId(), command.getQuestion());
        // 校验命令
        if (command.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        // 2. 调用AI服务获取回答
        String answer = geminiClient.askQuestion(command.getQuestion());

        command.setAnswer(answer);

        // 使用工厂方法创建领域对象：保持领域模型的完整性
        QAHistory history = QAHistory.createNew(
                command.getUserId(),
                command.getQuestion(),
                answer
//                command.getSessionId()
//                rag
        );
        
        qaHistoryRepo.save(history);
        QAHistoryDto response = qaMapper.toDto(history);

        log.info("问答处理完成");
        
        return response;
    }

    //获取问答历史
    public List<QAHistoryDto> queryUserHistory(QAHistoryQuery query){
        log.info("查询用户问答历史，用户ID: {}", query.getUserId());
        List<QAHistory> histories;

//        if (query.getSessionId() != null) {
//        	histories = qaHistoryRepo.findHistoryBySessionId(query.getSessionId());
//        } else {
        	histories = qaHistoryRepo.findHistoryByUserId(query.getUserId());
//        }
        log.info("查询用户问答历史完成，用户ID: {}, 记录数: {}", query.getUserId(), histories.size());

        List<QAHistoryDto> responses = histories.stream()
                .map(qaMapper::toDto)
                .collect(Collectors.toList());
        return responses;
    }
}
