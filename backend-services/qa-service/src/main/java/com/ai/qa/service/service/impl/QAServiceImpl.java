package com.ai.qa.service.service.impl;

import com.ai.qa.service.client.AIModelClient;
import com.ai.qa.service.dto.QuestionRequest;
import com.ai.qa.service.dto.QuestionResponse;
import com.ai.qa.service.entity.QARecord;
import com.ai.qa.service.repository.QARecordRepository;
import com.ai.qa.service.service.QAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QAServiceImpl implements QAService {

    @Autowired
    private AIModelClient aiModelClient;

    @Autowired
    private QARecordRepository qaRecordRepository;

    @Override
    public QuestionResponse processQuestion(QuestionRequest request) {
        long startTime = System.currentTimeMillis();

        // 输入验证
        if (!StringUtils.hasText(request.getQuestion())) {
            throw new IllegalArgumentException("问题内容不能为空");
        }

        // 生成会话ID（如果没有提供）
        if (!StringUtils.hasText(request.getSessionId())) {
            request.setSessionId("session_" + UUID.randomUUID().toString().substring(0, 8));
        }

        try {
            // 调用AI模型获取答案
            String answer = aiModelClient.getAIResponse(request.getQuestion());

            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;

            // 创建问答记录
            QARecord qaRecord = new QARecord();
            qaRecord.setQuestion(request.getQuestion());
            qaRecord.setAnswer(answer);
            qaRecord.setUserId(request.getUserId());
            qaRecord.setSessionId(request.getSessionId());
            qaRecord.setQuestionType(request.getQuestionType());
            qaRecord.setResponseTime(responseTime);
            qaRecord.setModelVersion("gemini-pro");
            qaRecord.setCreateTime(LocalDateTime.now());

            // 保存到数据库
            QARecord savedRecord = qaRecordRepository.save(qaRecord);

            // 构建响应
            return convertToResponse(savedRecord);

        } catch (Exception e) {
            // 记录错误日志
            System.err.println("处理问题时发生错误: " + e.getMessage());

            // 创建错误响应记录
            QARecord errorRecord = new QARecord();
            errorRecord.setQuestion(request.getQuestion());
            errorRecord.setAnswer("抱歉，处理您的问题时遇到了技术问题，请稍后重试。");
            errorRecord.setUserId(request.getUserId());
            errorRecord.setSessionId(request.getSessionId());
            errorRecord.setQuestionType(request.getQuestionType());
            errorRecord.setResponseTime(System.currentTimeMillis() - startTime);
            errorRecord.setModelVersion("error");
            errorRecord.setCreateTime(LocalDateTime.now());

            QARecord savedErrorRecord = qaRecordRepository.save(errorRecord);
            return convertToResponse(savedErrorRecord);
        }
    }

    @Override
    public List<QuestionResponse> getQuestionHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<QARecord> qaRecords = qaRecordRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);

        return qaRecords.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getSessionHistory(String sessionId) {
        List<QARecord> qaRecords = qaRecordRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);

        return qaRecords.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuestion(Long qaId) {
        if (!qaRecordRepository.existsById(qaId)) {
            throw new IllegalArgumentException("问答记录不存在");
        }
        qaRecordRepository.deleteById(qaId);
    }

    private QuestionResponse convertToResponse(QARecord qaRecord) {
        QuestionResponse response = new QuestionResponse();
        response.setId(qaRecord.getId());
        response.setQuestion(qaRecord.getQuestion());
        response.setAnswer(qaRecord.getAnswer());
        response.setUserId(qaRecord.getUserId());
        response.setSessionId(qaRecord.getSessionId());
        response.setResponseTime(qaRecord.getResponseTime());
        response.setCreateTime(qaRecord.getCreateTime());
        response.setQuestionType(qaRecord.getQuestionType());
        response.setModelVersion(qaRecord.getModelVersion());
        return response;
    }
}
