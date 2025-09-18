package com.ai.qa.service.domain.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.infrastructure.feign.UserClient;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.entities.QASessionPO;
import com.ai.qa.service.infrastructure.persistence.repositories.QAHistoryRepoImpl;
import com.ai.qa.service.infrastructure.persistence.repositories.QASessionRepoImpl;
import com.ai.qa.service.domain.exception.QAHistoryNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QAService {
    private final QAHistoryRepoImpl qaHistoryRepo;
    private final QASessionRepoImpl qaSessionRepo;
    private final UserClient userClient;
    private final RedisService redisService;

    public String createSessionId(String userId) {
        if(!checkUserId(userId)){
            throw new RuntimeException("无效的用户ID");
        }
        // 生成唯一会话 ID
        String sessionId = UUID.randomUUID().toString();

        // 存储到 Redis
        redisService.saveSession(sessionId, userId);

        return sessionId;
    }

    public List<QAHistorySession> getHistorySessionId(String userId) {
        List<QASessionPO> sessionList = qaSessionRepo.findByUserId(userId);
        List<QAHistorySession> qaHistorySessionList = new ArrayList<>();
        if (sessionList.isEmpty()) {
            throw new QAHistoryNotFoundException(userId);
        }
        for(QASessionPO session : sessionList){
            qaHistorySessionList.add(QAHistorySession.getInstance(session.getUserId(), session.getSessionId(), session.getTopic()));
        }
        return qaHistorySessionList;
    }

    public List<QAHistory> getHistoryBySessionId(String sessionId) {
        List<QAHistoryPO> historyList = qaHistoryRepo.findBySessionId(sessionId);
        List<QAHistory> qaHistoryList = new ArrayList<>();
        if (historyList.isEmpty()) {
            throw new QAHistoryNotFoundException(sessionId);
        }
        for(QAHistoryPO history : historyList){
            qaHistoryList.add(QAHistory.getInstance(history.getUserId(), history.getSessionId(), history.getQuestion(), history.getAnswer(), history.getCreateTime()));
        }
        return qaHistoryList;
    }

    public void saveQAHistory(String userId, String sessionId, String question, String answer, LocalDateTime timestamp) {
        QAHistory qaHistory = QAHistory.getInstance(userId, sessionId, question, answer, timestamp);
        qaHistory.validate();
        qaHistoryRepo.save(qaHistory);
    }

    public int delQAHistory(String sessionId) {
        int delSessionResult = qaSessionRepo.delete(sessionId);
        int delHistoryResult = qaHistoryRepo.delete(sessionId);
        return delSessionResult == 1 && delHistoryResult == 1 ? 1 : 0;
    }
 
    private boolean checkUserId(String userId){
        try {
            // 就像调用一个本地方法一样！
            String strExist = userClient.getUserById(Long.valueOf(userId));
            if("exist".equals(strExist)){
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // Feign 在遇到 4xx/5xx 错误时会抛出异常，需要处理
            throw new RuntimeException("Failed to fetch user info for userId: " + userId + ". Error: " + e.getMessage());
        }
    }

    public String processQuestion(Long userId) {
        // 1. 调用 user-service 获取用户信息
        System.out.println("Fetching user info for userId: " + userId);
        String user;
        try {

            // 就像调用一个本地方法一样！
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            // Feign 在遇到 4xx/5xx 错误时会抛出异常，需要处理
            System.err.println("Failed to fetch user info for userId: " + userId + ". Error: " + e.getMessage());
            // 可以根据业务返回一个默认的、友好的错误信息
            return "Sorry, I cannot get your user information right now.";
        }

        if (user == null) {
            return "Sorry, user with ID " + userId + " not found.";
        }

        System.out.println("Question from user: " + user);

        // 返回最终结果
        return user;
    }
}
