package com.ai.qa.qa.application.impl;

import com.ai.qa.qa.application.QAService;
import com.ai.qa.qa.api.dto.QARequest;
import com.ai.qa.qa.api.dto.QAResponse;
import com.ai.qa.qa.api.feign.UserServiceFeignClient;
import com.ai.qa.qa.api.feign.dto.User;
import com.ai.qa.qa.domain.entity.QA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;

@Service
public class QAServiceImpl implements QAService {
    
    @Autowired
    private UserServiceFeignClient userServiceFeignClient;
    
    // 模拟数据库存储
    private static final ConcurrentHashMap<Long, QA> qaDatabase = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    @Transactional
    public QAResponse askQuestion(QARequest request) {
        // 创建问答记录
        QA qa = new QA();
        qa.setId(idGenerator.getAndIncrement());
        qa.setUserId(request.getUserId());
        qa.setQuestion(request.getQuestion());
        // 模拟AI回答
        qa.setAnswer("这是对问题 \"" + request.getQuestion() + "\" 的模拟回答。在真实系统中，这里会是AI生成的答案。");
        qa.setCreateTime(LocalDateTime.now());
        
        // 保存到数据库
        qaDatabase.put(qa.getId(), qa);
        
        // 转换为响应对象
        QAResponse response = new QAResponse();
        response.setId(qa.getId());
        response.setQuestion(qa.getQuestion());
        response.setAnswer(qa.getAnswer());
        response.setCreateTime(qa.getCreateTime());
        
        // 通过Feign调用user-service获取用户名
        if (request.getUserId() != null) {
            try {
                com.ai.qa.qa.api.dto.Response<User> userResponse = userServiceFeignClient.getUserById(request.getUserId());
                if (userResponse != null && "SUCCESS".equals(userResponse.getResult()) && userResponse.getData() != null) {
                    response.setUsername(userResponse.getData().getUsername());
                }
            } catch (Exception e) {
                // 如果调用失败，记录日志但不中断主流程
                e.printStackTrace();
            }
        }
        
        return response;
    }
    
    @Override
    public List<QAResponse> getQAHistory(Long userId) {
        List<QAResponse> history = new ArrayList<>();
        for (QA qa : qaDatabase.values()) {
            if (qa.getUserId().equals(userId)) {
                QAResponse response = new QAResponse();
                response.setId(qa.getId());
                response.setQuestion(qa.getQuestion());
                response.setAnswer(qa.getAnswer());
                response.setCreateTime(qa.getCreateTime());
                
                // 通过Feign调用user-service获取用户名
                if (userId != null) {
                    try {
                        com.ai.qa.qa.api.dto.Response<User> userResponse = userServiceFeignClient.getUserById(userId);
                        if (userResponse != null && "SUCCESS".equals(userResponse.getResult()) && userResponse.getData() != null) {
                            response.setUsername(userResponse.getData().getUsername());
                        }
                    } catch (Exception e) {
                        // 如果调用失败，记录日志但不中断主流程
                        e.printStackTrace();
                    }
                }
                
                history.add(response);
            }
        }
        return history;
    }
    
    @Override
    public QA getQAById(Long id) {
        return qaDatabase.get(id);
    }
}