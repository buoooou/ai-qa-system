package com.ai.qa.service.application.service;

import java.time.LocalDateTime;
import java.util.List;

import com.ai.qa.service.application.dto.QAHistoryDTO;
import com.ai.qa.service.application.dto.QAHistorySessionDTO;
import com.ai.qa.service.domain.exception.QAHistoryNotFoundException;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.model.QAHistorySession;
import com.ai.qa.service.domain.service.QAService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QAHistoryService {

    private final QAService qaService;

    public String testFeign() {
        return qaService.processQuestion(1L);
    }

    /**
     * 创建新会话
     * @param userId
     * @return
     */
    public QAHistorySessionDTO initQAHistory(String userId) {
        // 生成新SessionId并保存到Redis中
        String sessionId = qaService.saveSessionId(userId);
        // 获取历史SessionId列表
        List<QAHistorySession> qaHistorySessionList = qaService.getHistorySessionId(userId);
        return QAHistorySessionDTO.fromDomain(userId, sessionId, qaHistorySessionList);
    }

    /**
     * 新建会话时保存前次会话的主题
     * @param userId
     * @param sessionId
     * @param topic
     */
    public void createQATopic(String userId, String sessionId, String topic){
        qaService.createQATopic(userId, sessionId, topic);
    }

    /**
     * 系统退出时清除缓存中的SessionId
     * @param sessionId
     * @return
     */
    public boolean delSessionId(String sessionId) {
        return qaService.delSessionId(sessionId);
    }

    @Transactional
    public void saveHistory(String userId, String sessionId, String question, String answer) {
        qaService.saveQAHistory(userId, sessionId, question, answer, LocalDateTime.now());
    }

    @Transactional
    public int delHistory(String sessionId) {
        return qaService.delQAHistory(sessionId);
    }

    public QAHistoryDTO getHistoryBySessionId(String userId, String currentSid, String specifySid) {
        // 将指定会话的SessionId切换到当前会话的SessionId
        qaService.switchToSpecifySId(userId, currentSid, specifySid);
        // 获取指定会话SessionId的会话历史消息
        List<QAHistory> qaHistoryList = qaService.getHistoryBySessionId(specifySid);
        if(qaHistoryList.isEmpty()) {
            throw new QAHistoryNotFoundException(specifySid);
        }

        return QAHistoryDTO.fromDomain(userId, specifySid, qaHistoryList);
    }

    public boolean existSessionId(String sessionId) {
        return qaService.existSessionId(sessionId);
    }
}
