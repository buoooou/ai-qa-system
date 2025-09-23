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

import jakarta.transaction.Transactional;

import com.ai.qa.service.domain.exception.QAHistoryNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * 问答服务类，提供问答历史记录和会话管理的核心业务逻辑。
 */
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QAService {
    private final QAHistoryRepoImpl qaHistoryRepo;
    private final QASessionRepoImpl qaSessionRepo;
    private final UserClient userClient;
    private final RedisService redisService;

        /**
     * 保存会话ID到Redis，并返回生成的会话ID。
     * @param userId 用户ID
     * @return 生成的会话ID
     */
    public String saveSessionId(String userId) {
        // 因为nacos没有注册服务无法调用负载均衡 故注释掉以下
        // if(!checkUserId(userId)){
        //     throw new RuntimeException("无效的用户ID");
        // }

        // 创建会话Id
        String sessionId = UUID.randomUUID().toString();

        // 存储到 Redis
        redisService.saveSession(sessionId, userId);

        return sessionId;
    }

    public void switchToSpecifySId(String userId, String currentSid, String specifySid){
        // 删除当前会话SessionId
        redisService.deleteSession(currentSid);
        // 将指定的历史会话SessionId，存储到 Redis
        redisService.saveSession(specifySid, userId);
    }

    @Transactional
    public void createQATopic(String userId, String sessionId, String topic){
        QAHistorySession qaHistorySession = QAHistorySession.getInstance(userId, sessionId, topic);
        qaSessionRepo.save(qaHistorySession);
    }

    public boolean delSessionId(String sessionId){
        return redisService.deleteSession(sessionId);
    }

    public boolean existSessionId(String sessionId){
        return qaSessionRepo.findBySessionId(sessionId);
    }

    public List<QAHistorySession> getHistorySessionId(String userId) {
        List<QASessionPO> sessionList = qaSessionRepo.findByUserId(userId);
        List<QAHistorySession> qaHistorySessionList = new ArrayList<>();

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

    @Transactional
    public void saveQAHistory(String userId, String sessionId, String question, String answer, LocalDateTime timestamp) {
        QAHistory qaHistory = QAHistory.getInstance(userId, sessionId, question, answer, timestamp);
        qaHistory.validate();
        qaHistoryRepo.save(qaHistory);
    }

    @Transactional
    public int delQAHistory(String sessionId) {
        int delSessionResult = qaSessionRepo.delete(sessionId);
        int delHistoryResult = qaHistoryRepo.delete(sessionId);
        return delSessionResult == 1 && delHistoryResult == 1 ? 1 : 0;
    }
 
    public boolean checkUserId(String userId){
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
