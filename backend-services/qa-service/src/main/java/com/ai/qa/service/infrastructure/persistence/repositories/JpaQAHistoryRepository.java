package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;

@Repository
public interface JpaQAHistoryRepository extends JpaRepository<QAHistoryPO, Long> {

    /**
     * 根据主键ID查找QA历史记录 此方法由JPA自动实现，无需编写SQL
     *
     * @param id 主键ID
     * @return 包含QA历史记录的Optional对象
     */
    Optional<QAHistoryPO> findHistoryById(Long id);

    /**
     * 根据用户ID查找所有QA历史记录，按时间降序排列 使用JPQL自定义查询语句
     *
     * @param userId 用户ID
     * @return 该用户的所有QA历史记录列表，按时间倒序排列
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId ORDER BY q.create_time DESC")
    List<QAHistoryPO> findByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID分页查找QA历史记录 支持分页和排序，提高大数据量查询性能
     *
     * @param userId 用户ID
     * @param pageable 分页参数，包含页码、每页大小和排序信息
     * @return 分页后的QA历史记录列表
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId")
    List<QAHistoryPO> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据会话ID查找所有QA历史记录，按时间降序排列 用于获取同一会话中的完整对话历史
     *
     * @param sessionId 会话ID
     * @return 该会话的所有QA历史记录列表，按时间倒序排列
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.sessionId = :sessionId ORDER BY q.create_time DESC")
    List<QAHistoryPO> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据用户ID和会话ID联合查找QA历史记录，按时间降序排列 用于精确获取特定用户在特定会话中的问答记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的QA历史记录列表，按时间倒序排列
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId AND q.sessionId = :sessionId ORDER BY q.create_time DESC")
    List<QAHistoryPO> findByUserIdAndSessionId(@Param("userId") Long userId, @Param("sessionId") String sessionId);

    /**
     * 统计指定用户的问答记录数量 用于用户行为分析和统计报表
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    @Query("SELECT COUNT(q) FROM QAHistoryPO q WHERE q.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 根据会话ID统计问答记录数量 用于会话长度监控和清理策略
     *
     * @param sessionId 会话ID
     * @return 该会话的问答记录总数
     */
    @Query("SELECT COUNT(q) FROM QAHistoryPO q WHERE q.sessionId = :sessionId")
    long countBySessionId(@Param("sessionId") String sessionId);

    /**
     * 删除指定用户的全部问答记录 用于用户数据清理或账号注销功能
     *
     * @param userId 用户ID
     * @return 删除的记录数量
     */
    @Query("DELETE FROM QAHistoryPO q WHERE q.userId = :userId")
    long deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除指定会话的全部问答记录 用于会话数据清理和维护
     *
     * @param sessionId 会话ID
     * @return 删除的记录数量
     */
    @Query("DELETE FROM QAHistoryPO q WHERE q.sessionId = :sessionId")
    long deleteBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据关键词搜索用户的问答历史
     * 
     * 在问题和答案中搜索包含指定关键词的记录
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return List<QAHistoryPO> 包含关键词的问答历史
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId " +
           "AND (q.question LIKE %:keyword% OR q.answer LIKE %:keyword%) " +
           "ORDER BY q.createTime DESC")
    List<QAHistoryPO> searchByKeyword(@Param("userId") Long userId, 
                                   @Param("keyword") String keyword);

    /**
     * 根据用户ID分页查找问答历史
     * 
     * 用于前端分页显示，避免一次性加载过多数据
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return List<QAHistoryPO> 指定数量的问答历史
     */
    @Query("SELECT q FROM QAHistoryPO q WHERE q.userId = :userId ORDER BY q.createTime DESC")
    List<QAHistoryPO> findTopByUserId(@Param("userId") Long userId, 
                                   org.springframework.data.domain.Pageable pageable);
}
