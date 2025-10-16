package com.ai.qa.service.domain.repository;

import java.util.List;

import com.ai.qa.service.domain.model.QAHistory;

public interface QAHistoryRepo {

    /**
     * 保存QA历史记录
     *
     * @param history 要保存的QA历史记录
     * @return 保存后的QA历史记录（包含生成的ID）
     */
    QAHistory save(QAHistory history);

    /**
     * 根据ID查找QA历史记录
     *
     * @param id 记录ID
     * @return 包含QA历史记录的Optional对象
     */
    QAHistory findHistoryById(Long id);
    QAHistory findById(Long id);

    /**
     * 根据会话ID查找QA历史记录
     *
     * @param sessionId 会话ID
     * @return 该会话的所有QA历史记录列表
     */
    List<QAHistory> findHistoryBySessionId(String sessionId);

    /**
     * 根据用户ID查找QA历史记录
     *
     * @param userId 用户ID
     * @return 该用户的所有QA历史记录列表
     */
    List<QAHistory> findHistoryByUserId(Long userId);

    /**
     * 根据用户ID和会话ID查找QA历史记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的QA历史记录列表
     */
    List<QAHistory> findHistoryByUserIdAndSessionId(Long userId, String sessionId);

    /**
     * 删除QA历史记录
     *
     * @param id 要删除的记录ID
     */
    void deleteById(Long id);

    long deleteAll(Long userId);

    long deleteBySessionId(String sessionId);

    /**
     * 获取用户最近的QA历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 最近的QA历史记录列表
     */
    List<QAHistory> findRecentHistoryByUserId(Long userId, int limit);

    /**
     * 统计用户的问答数量
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    long countByUserId(Long userId);

    List<QAHistory> findTopByUserId(Long userId, int page, int size);

    List<QAHistory> searchByKeyword(Long userId, String keyword);
}
