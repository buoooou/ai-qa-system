package com.ai.qa.service.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ai.qa.service.api.dto.ApiResponse;
import com.ai.qa.service.api.dto.QARequest;
import com.ai.qa.service.api.dto.QAResponse;
import com.ai.qa.service.api.dto.UserInfoDTO;
import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repository.QAHistoryRepo;
import com.ai.qa.service.infrastructure.feign.GeminiClient;
import com.ai.qa.service.infrastructure.feign.UserClient;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QAService {

    private final UserClient userClient;

    private final GeminiClient geminiClient;

    private final QAHistoryRepo qaHistoryRepo;

    // public String processQuestion(Long userId) {
    //     // 1. 调用 user-service 获取用户信息
    //     System.out.println("Fetching user info for userId: " + userId);
    //     String user;
    //     try {

    //         // 就像调用一个本地方法一样！
    //         user = userClient.getUserById(userId);
    //     } catch (Exception e) {
    //         // Feign 在遇到 4xx/5xx 错误时会抛出异常，需要处理
    //         System.err.println("Failed to fetch user info for userId: " + userId + ". Error: " + e.getMessage());
    //         // 可以根据业务返回一个默认的、友好的错误信息
    //         return "Sorry, I cannot get your user information right now.";
    //     }

    //     if (user == null) {
    //         return "Sorry, user with ID " + userId + " not found.";
    //     }

    //     System.out.println("Question from user: " + user);

    //     // 返回最终结果
    //     return user;
    // }

    /**
     * 处理用户问答请求
     * 
     * 业务流程：
     * 1. 记录开始时间（用于性能监控）
     * 2. 调用AI服务获取回答
     * 3. 保存问答历史到数据库
     * 4. 构建并返回响应对象
     * 
     * @param request 问答请求
     * @return QaResponse 问答响应
     * @throws RuntimeException 当AI服务调用失败时抛出异常
     */
    public QAResponse askQuestion(QARequest request) {
        log.info("开始处理问答请求，用户ID: {}, 问题长度: {}", request.getUserId(), request.getQuestion().length());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 1. 获取用户信息（通过Feign Client调用user-service）
            UserInfoDTO userInfo = getUserInfo(request.getUserId());
            
            // 2. 构建包含用户信息的完整问题
            String fullQuestion = buildQuestionWithUserContext(request, userInfo);
            
            // 3. 调用AI服务获取回答
            String answer = geminiClient.askQuestion(fullQuestion);
            
            // 4. 个性化回答（包含用户信息）
            String personalizedAnswer = personalizeAnswer(answer, userInfo);
            
            // 5. 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 6. 保存问答历史
            QAHistory answerHistory = QAHistory.builder().userId(request.getUserId()).question(request.getQuestion())
                .sessionId(request.getSessionId()).answer(personalizedAnswer).build();
            QAHistory savedHistory = qaHistoryRepo.save(answerHistory);
            
            // 7. 构建响应对象
            QAResponse response = new QAResponse(
                savedHistory.getId(),
                savedHistory.getUserId(),
                savedHistory.getQuestion(),
                savedHistory.getAnswer(),
                savedHistory.getSessionId(),
                savedHistory.getCreateTime()
            );
            response.setModel("gemini-2.5-flash-lite");
            response.setResponseTime(responseTime);
            
            log.info("问答处理完成，用户ID: {}, 用户名: {}, 响应时间: {}ms", 
                    request.getUserId(), userInfo != null ? userInfo.getUsername() : "未知", responseTime);
            
            return response;
            
        } catch (Exception e) {
            log.error("处理问答请求失败，用户ID: {}, 错误信息: {}", 
                     request.getUserId(), e.getMessage(), e);
            throw new RuntimeException("问答服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 处理用户问题并返回答案
     * 完整的问答处理流程：获取用户信息 → 生成RAG上下文 → 生成答案 → 保存历史
     *
     * @param userId    用户名
     * @param question  用户问题
     * @param sessionId 会话ID
     * @return AI生成的答案
     */
    public String processQuestion(Long userId, String question, String sessionId) {
        // 1. 获取用户信息
        // String userInfo = getUserInfo(userId);

        // 2. 生成RAG上下文
        // QARAG rag = generateRAGContext(question, userId);

        // 3. 使用Gemini生成答案
        // String answer = generateAnswer(question);
        String answer = "";

        // 4. 保存问答历史
        // saveQAHistory(userId.toString(), question, answer, sessionId);

        return answer;
    }

    /**
     * 获取用户问答历史
     * 
     * @param userId 用户ID
     * @return List<QaResponse> 问答历史列表
     */
    public List<QAResponse> getUserHistory(Long userId) {
        log.info("查询用户问答历史，用户ID: {}", userId);
        
        List<QAHistory> histories = qaHistoryRepo.findHistoryByUserId(userId);
        
        List<QAResponse> responses = histories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("查询用户问答历史完成，用户ID: {}, 记录数: {}", userId, responses.size());
        return responses;
    }
    
    /**
     * 分页获取用户问答历史
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return List<QaResponse> 问答历史列表
     */
    public List<QAResponse> getUserHistoryPaged(Long userId, int page, int size) {
        log.info("分页查询用户问答历史，用户ID: {}, 页码: {}, 每页大小: {}", userId, page, size);
        
        List<QAHistory> histories = qaHistoryRepo.findTopByUserId(userId, page, size);
        
        List<QAResponse> responses = histories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("分页查询用户问答历史完成，用户ID: {}, 返回记录数: {}", userId, responses.size());
        return responses;
    }
    
    /**
     * 根据关键词搜索用户问答历史
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return List<QaResponse> 匹配的问答历史
     */
    public List<QAResponse> searchUserHistory(Long userId, String keyword) {
        log.info("搜索用户问答历史，用户ID: {}, 关键词: {}", userId, keyword);
        
        List<QAHistory> histories = qaHistoryRepo.searchByKeyword(userId, keyword);
        
        List<QAResponse> responses = histories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        log.info("搜索用户问答历史完成，用户ID: {}, 关键词: {}, 匹配记录数: {}", 
                userId, keyword, responses.size());
        return responses;
    }
    
    /**
     * 获取用户问答统计信息
     * 
     * @param userId 用户ID
     * @return long 问答总数
     */
    public long getUserQaCount(Long userId) {
        log.debug("查询用户问答总数，用户ID: {}", userId);
        
        long count = qaHistoryRepo.countByUserId(userId);
        
        log.debug("查询用户问答总数完成，用户ID: {}, 总数: {}", userId, count);
        return count;
    }
    
    /**
     * 根据ID获取问答记录
     * 
     * @param id 问答记录ID
     * @return QaResponse 问答记录（如果存在）
     */
    public QAResponse getQaById(Long id) {
        log.debug("根据ID查询问答记录，ID: {}", id);
        
        return Optional.of(qaHistoryRepo.findById(id)).map(this::convertToResponse).orElse(null);

    }
    
    /**
     * 构建包含上下文的完整问题
     * 
     * 获取用户最近的几条问答记录作为上下文，帮助AI更好地理解对话
     * 
     * @param request 问答请求
     * @return String 包含上下文的完整问题
     */
    private String buildQuestionWithContext(QARequest request) {
        // 如果请求中已经包含上下文，直接使用
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            return request.getContext() + "\n\n" + request.getQuestion();
        }
        
        // 获取用户最近的3条问答记录作为上下文
        List<QAHistory> recentHistories = qaHistoryRepo.findRecentHistoryByUserId(
            request.getUserId(), 3);
        
        if (recentHistories.isEmpty()) {
            // 如果没有历史记录，直接返回问题
            return request.getQuestion();
        }
        
        // 构建上下文
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("以下是我们之前的对话历史：\n");
        
        for (int i = recentHistories.size() - 1; i >= 0; i--) {
            QAHistory history = recentHistories.get(i);
            contextBuilder.append("用户：").append(history.getQuestion()).append("\n");
            contextBuilder.append("助手：").append(history.getAnswer()).append("\n\n");
        }
        
        contextBuilder.append("现在的问题是：\n").append(request.getQuestion());
        
        return contextBuilder.toString();
    }
    
    /**
     * 删除问答记录
     * 
     * @param id 问答记录ID
     * @param userId 用户ID（用于权限验证）
     * @return boolean true-删除成功，false-删除失败或记录不存在
     */
    public boolean deleteQaRecord(Long id, Long userId) {
        log.info("删除问答记录，记录ID: {}, 用户ID: {}", id, userId);
        
        try {
            // 先查询记录是否存在且属于该用户
            QAHistory QAHistory = qaHistoryRepo.findById(id);
            if (QAHistory == null) {
                log.warn("问答记录不存在，记录ID: {}", id);
                return false;
            }
            
            if (!QAHistory.getUserId().equals(userId)) {
                log.warn("用户无权删除该问答记录，记录ID: {}, 记录所属用户: {}, 请求用户: {}", 
                        id, QAHistory.getUserId(), userId);
                return false;
            }
            
            // 删除记录
            qaHistoryRepo.deleteById(id);
            log.info("问答记录删除成功，记录ID: {}", id);
            return true;
            
        } catch (Exception e) {
            log.error("删除问答记录失败，记录ID: {}, 错误信息: {}", id, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 批量删除用户问答记录
     * 
     * @param userId 用户ID
     * @return int 删除的记录数量
     */
    public int deleteUserAllQaRecords(Long userId) {
        log.info("批量删除用户问答记录，用户ID: {}", userId);
        
        try {
            // 先查询用户的所有记录
            List<QAHistory> userHistories = qaHistoryRepo.findHistoryByUserId(userId);
            int count = userHistories.size();
            
            if (count == 0) {
                log.info("用户没有问答记录，用户ID: {}", userId);
                return 0;
            }
            
            // 批量删除
            qaHistoryRepo.deleteAll(userId);
            
            log.info("批量删除用户问答记录完成，用户ID: {}, 删除数量: {}", userId, count);
            return count;
            
        } catch (Exception e) {
            log.error("批量删除用户问答记录失败，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw new RuntimeException("删除用户问答记录失败");
        }
    }

    /**
     * 批量删除用户问答记录
     * 
     * @param sessionId 会话ID
     * @return int 删除的记录数量
     */
    public long deleteAllQaRecordsBySessionId(String sessionId) {
        log.info("批量删除会话ID的问答记录，会话ID: {}", sessionId);
        
        try {
            // 批量删除
            long count = qaHistoryRepo.deleteBySessionId(sessionId);

            if (count == 0) {
                log.info("该会话ID没有问答记录，会话ID: {}", sessionId);
                return 0;
            }
            
            log.info("批量删除会话ID的问答记录完成，会话ID: {}, 删除数量: {}", sessionId, count);
            return count;
            
        } catch (Exception e) {
            log.error("批量删除会话ID的问答记录失败，会话ID: {}, 错误信息: {}", sessionId, e.getMessage(), e);
            throw new RuntimeException("删除会话ID的问答记录失败");
        }
    }
    
    /**
     * 获取用户信息
     * 
     * 通过Feign Client调用user-service获取用户信息
     * 
     * @param userId 用户ID
     * @return UserInfoDto 用户信息（如果获取失败返回null）
     */
    private UserInfoDTO getUserInfo(Long userId) {
        try {
            log.debug("通过Feign Client获取用户信息，用户ID: {}", userId);
            
            ApiResponse<UserInfoDTO> response = userClient.getUserById(userId);
            
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                log.debug("成功获取用户信息，用户ID: {}, 用户名: {}", 
                         userId, response.getData().getUsername());
                return response.getData();
            } else {
                log.warn("获取用户信息失败，用户ID: {}, 响应: {}", userId, response);
                return null;
            }
            
        } catch (Exception e) {
            log.error("调用用户服务失败，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 构建包含用户信息的完整问题
     * 
     * 结合用户信息和历史上下文构建更完整的问题
     * 
     * @param request 问答请求
     * @param userInfo 用户信息
     * @return String 包含用户信息的完整问题
     */
    private String buildQuestionWithUserContext(QARequest request, UserInfoDTO userInfo) {
        StringBuilder questionBuilder = new StringBuilder();
        
        // 添加用户信息上下文
        if (userInfo != null) {
            questionBuilder.append("用户信息：\n");
            questionBuilder.append("- 用户名: ").append(userInfo.getUsername()).append("\n");
        }
        
        // 添加历史对话上下文
        String contextQuestion = buildQuestionWithContext(request);
        questionBuilder.append(contextQuestion);
        
        return questionBuilder.toString();
    }
    
    /**
     * 个性化回答
     * 
     * 根据用户信息对AI回答进行个性化处理
     * 
     * @param answer 原始AI回答
     * @param userInfo 用户信息
     * @return String 个性化后的回答
     */
    private String personalizeAnswer(String answer, UserInfoDTO userInfo) {
        if (userInfo == null || userInfo.getUsername() == null) {
            return answer;
        }
        
        // 在回答前添加个性化问候
        StringBuilder personalizedAnswer = new StringBuilder();
        personalizedAnswer.append("你好，").append(userInfo.getUsername()).append("！\n\n");
        personalizedAnswer.append(answer);
        
        // 在回答末尾添加个性化结尾
        personalizedAnswer.append("\n\n如果您还有其他问题，随时可以问我哦！");
        
        return personalizedAnswer.toString();
    }
    
    /**
     * 将QAHistory实体转换为QaResponse DTO
     * 
     * @param history 问答历史实体
     * @return QaResponse 问答响应DTO
     */
    private QAResponse convertToResponse(QAHistory history) {
        return new QAResponse(
            history.getId(),
            history.getUserId(),
            history.getQuestion(),
            history.getAnswer(),
            history.getSessionId(),
            history.getCreateTime()
        );
    }
}
