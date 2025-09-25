package com.ai.qa.service.service;

import com.ai.qa.service.client.GeminiClient;
//import com.ai.qa.service.client.UserServiceClient;
import com.ai.qa.service.dto.ApiResponse;
import com.ai.qa.service.dto.QaRequest;
import com.ai.qa.service.dto.QaResponse;
import com.ai.qa.service.dto.UserInfoDto;
import com.ai.qa.service.entity.QaHistory;
import com.ai.qa.service.repository.QaHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 问答服务实现类
 *
 * 实现IQaService接口，提供问答相关的具体业务逻辑：
 * 1. 调用AI服务获取回答
 * 2. 保存问答历史记录
 * 3. 查询用户问答历史
 * 4. 管理对话上下文
 *
 * 设计模式：
 * - 面向接口编程：实现IQaService接口
 * - 依赖注入：通过构造函数注入依赖
 * - 单例模式：Spring管理的单例Bean
 *
 * @author David
 * @version 1.0
 * @since 2025-09-06
 */
@Slf4j                      // Lombok注解：自动生成日志对象
@Service                    // Spring注解：标识这是一个服务层组件
@RequiredArgsConstructor    // Lombok注解：为final字段生成构造函数
public class QaServiceImpl implements QaService {

    /**
     * Gemini AI客户端
     */
    private final GeminiClient geminiClient;

    /**
     * 用户服务客户端
     */
    //private final UserServiceClient userServiceClient;

    /**
     * 问答历史数据访问层
     */
    private final QaHistoryRepository qaHistoryRepository;

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
    @Override
    @Transactional  // 事务注解：确保数据一致性
    public QaResponse askQuestion(QaRequest request) {
        log.info("开始处理问答请求，用户ID: {}, 问题长度: {}",
                request.getUserId(), request.getQuestion().length());

        long startTime = System.currentTimeMillis();

        try {
            // 1. 获取用户信息（通过Feign Client调用user-service）
            UserInfoDto userInfo = getUserInfo(request.getUserId());

            // 2. 构建包含用户信息的完整问题
            String fullQuestion = buildQuestionWithUserContext(request, userInfo);

            // 3. 调用AI服务获取回答
            String answer = geminiClient.askQuestion(fullQuestion);

            // 4. 个性化回答（包含用户信息）
            String personalizedAnswer = personalizeAnswer(answer, userInfo);

            // 5. 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;

            // 6. 保存问答历史
            QaHistory qaHistory = new QaHistory(
                    request.getUserId(),
                    request.getQuestion(),
                    personalizedAnswer
            );
            QaHistory savedHistory = qaHistoryRepository.save(qaHistory);

            // 7. 构建响应对象
            QaResponse response = new QaResponse(
                    savedHistory.getId(),
                    savedHistory.getUserId(),
                    savedHistory.getQuestion(),
                    savedHistory.getAnswer(),
                    savedHistory.getCreateTime()
            );
            response.setModel("gemini-pro");
            response.setResponseTime(responseTime);

            log.info("问答处理完成，用户ID: {}, 用户名: {}, 响应时间: {}ms",
                    request.getUserId(), userInfo != null ? userInfo.getUserName() : "未知", responseTime);

            return response;

        } catch (Exception e) {
            log.error("处理问答请求失败，用户ID: {}, 错误信息: {}",
                    request.getUserId(), e.getMessage(), e);
            throw new RuntimeException("问答服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 获取用户问答历史
     *
     * @param userId 用户ID
     * @return List<QaResponse> 问答历史列表
     */
    @Override
    public List<QaResponse> getUserHistory(Long userId) {
        log.info("查询用户问答历史，用户ID: {}", userId);

        List<QaHistory> histories = qaHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);

        List<QaResponse> responses = histories.stream()
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
    @Override
    public List<QaResponse> getUserHistoryPaged(Long userId, int page, int size) {
        log.info("分页查询用户问答历史，用户ID: {}, 页码: {}, 每页大小: {}", userId, page, size);

        PageRequest pageRequest = PageRequest.of(page, size);
        List<QaHistory> histories = qaHistoryRepository.findTopByUserId(userId, pageRequest);

        List<QaResponse> responses = histories.stream()
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
    @Override
    public List<QaResponse> searchUserHistory(Long userId, String keyword) {
        log.info("搜索用户问答历史，用户ID: {}, 关键词: {}", userId, keyword);

        List<QaHistory> histories = qaHistoryRepository.searchByKeyword(userId, keyword);

        List<QaResponse> responses = histories.stream()
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
    @Override
    public long getUserQaCount(Long userId) {
        log.debug("查询用户问答总数，用户ID: {}", userId);

        long count = qaHistoryRepository.countByUserId(userId);

        log.debug("查询用户问答总数完成，用户ID: {}, 总数: {}", userId, count);
        return count;
    }

    /**
     * 根据ID获取问答记录
     *
     * @param id 问答记录ID
     * @return QaResponse 问答记录（如果存在）
     */
    @Override
    public QaResponse getQaById(Long id) {
        log.debug("根据ID查询问答记录，ID: {}", id);

        return qaHistoryRepository.findById(id)
                .map(this::convertToResponse)
                .orElse(null);
    }

    /**
     * 构建包含上下文的完整问题
     *
     * 获取用户最近的几条问答记录作为上下文，帮助AI更好地理解对话
     *
     * @param request 问答请求
     * @return String 包含上下文的完整问题
     */
    private String buildQuestionWithContext(QaRequest request) {
        // 如果请求中已经包含上下文，直接使用
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            return request.getContext() + "\n\n" + request.getQuestion();
        }

        // 获取用户最近的3条问答记录作为上下文
        PageRequest pageRequest = PageRequest.of(0, 3);
        List<QaHistory> recentHistories = qaHistoryRepository.findRecentByUserId(
                request.getUserId(), pageRequest);

        if (recentHistories.isEmpty()) {
            // 如果没有历史记录，直接返回问题
            return request.getQuestion();
        }

        // 构建上下文
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("以下是我们之前的对话历史：\n");

        for (int i = recentHistories.size() - 1; i >= 0; i--) {
            QaHistory history = recentHistories.get(i);
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
    @Override
    @Transactional
    public boolean deleteQaRecord(Long id, Long userId) {
        log.info("删除问答记录，记录ID: {}, 用户ID: {}", id, userId);

        try {
            // 先查询记录是否存在且属于该用户
            QaHistory qaHistory = qaHistoryRepository.findById(id).orElse(null);
            if (qaHistory == null) {
                log.warn("问答记录不存在，记录ID: {}", id);
                return false;
            }

            if (!qaHistory.getUserId().equals(userId)) {
                log.warn("用户无权删除该问答记录，记录ID: {}, 记录所属用户: {}, 请求用户: {}",
                        id, qaHistory.getUserId(), userId);
                return false;
            }

            // 删除记录
            qaHistoryRepository.deleteById(id);
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
    @Override
    @Transactional
    public int deleteUserAllQaRecords(Long userId) {
        log.info("批量删除用户问答记录，用户ID: {}", userId);

        try {
            // 先查询用户的所有记录
            List<QaHistory> userHistories = qaHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);
            int count = userHistories.size();

            if (count == 0) {
                log.info("用户没有问答记录，用户ID: {}", userId);
                return 0;
            }

            // 批量删除
            qaHistoryRepository.deleteAll(userHistories);

            log.info("批量删除用户问答记录完成，用户ID: {}, 删除数量: {}", userId, count);
            return count;

        } catch (Exception e) {
            log.error("批量删除用户问答记录失败，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            throw new RuntimeException("删除用户问答记录失败");
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
    private UserInfoDto getUserInfo(Long userId) {
        try {
            log.debug("通过Feign Client获取用户信息，用户ID: {}", userId);

            //ApiResponse<UserInfoDto> response = userServiceClient.getUserById(userId);
            ApiResponse<UserInfoDto> response = new ApiResponse<>();
            response.setCode(200);
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUserName("david");
            userInfoDto.setEmail("123@gmail.com");
            userInfoDto.setId(1L);
            userInfoDto.setStatus("Active");
            response.setData(userInfoDto);

            if (response != null && response.getCode() == 200 && response.getData() != null) {
                log.debug("成功获取用户信息，用户ID: {}, 用户名: {}",
                        userId, response.getData().getUserName());
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
    private String buildQuestionWithUserContext(QaRequest request, UserInfoDto userInfo) {
        StringBuilder questionBuilder = new StringBuilder();

        // 添加用户信息上下文
        if (userInfo != null) {
            questionBuilder.append("用户信息：\n");
            questionBuilder.append("- 用户名: ").append(userInfo.getUserName()).append("\n");
            questionBuilder.append("- 邮箱: ").append(userInfo.getEmail()).append("\n");
            questionBuilder.append("- 状态: ").append(userInfo.getStatus()).append("\n\n");
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
    private String personalizeAnswer(String answer, UserInfoDto userInfo) {
        if (userInfo == null || userInfo.getUserName() == null) {
            return answer;
        }

        // 在回答前添加个性化问候
        StringBuilder personalizedAnswer = new StringBuilder();
        personalizedAnswer.append("你好，").append(userInfo.getUserName()).append("！\n\n");
        personalizedAnswer.append(answer);

        // 在回答末尾添加个性化结尾
        personalizedAnswer.append("\n\n如果您还有其他问题，随时可以问我哦！");

        return personalizedAnswer.toString();
    }

    /**
     * 将QaHistory实体转换为QaResponse DTO
     *
     * @param history 问答历史实体
     * @return QaResponse 问答响应DTO
     */
    private QaResponse convertToResponse(QaHistory history) {
        return new QaResponse(
                history.getId(),
                history.getUserId(),
                history.getQuestion(),
                history.getAnswer(),
                history.getCreateTime()
        );
    }
}