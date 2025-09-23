package com.ai.qa.service.repository;

import com.ai.qa.service.entity.QaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 问答历史数据访问层接口
 * 
 * 继承JpaRepository，提供基本的CRUD操作
 * 包含自定义查询方法，用于问答历史的管理和查询
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@Repository  // Spring注解：标识这是一个数据访问层组件
public interface QaHistoryRepository extends JpaRepository<QaHistory, Long> {
    
    /**
     * 根据用户ID查找问答历史
     * 
     * 按创建时间倒序排列，最新的问答记录在前面
     * 
     * @param userId 用户ID
     * @return List<QaHistory> 该用户的所有问答历史
     */
    List<QaHistory> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据用户ID分页查找问答历史
     * 
     * 用于前端分页显示，避免一次性加载过多数据
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return List<QaHistory> 指定数量的问答历史
     */
    @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId ORDER BY q.createTime DESC")
    List<QaHistory> findTopByUserId(@Param("userId") Long userId, 
                                   org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查找问答历史
     * 
     * 用于查询特定时间段内的问答记录
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<QaHistory> 指定时间范围内的问答历史
     */
    @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId " +
           "AND q.createTime BETWEEN :startTime AND :endTime " +
           "ORDER BY q.createTime DESC")
    List<QaHistory> findByUserIdAndCreateTimeBetween(@Param("userId") Long userId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据关键词搜索用户的问答历史
     * 
     * 在问题和答案中搜索包含指定关键词的记录
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return List<QaHistory> 包含关键词的问答历史
     */
    @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId " +
           "AND (q.question LIKE %:keyword% OR q.answer LIKE %:keyword%) " +
           "ORDER BY q.createTime DESC")
    List<QaHistory> searchByKeyword(@Param("userId") Long userId, 
                                   @Param("keyword") String keyword);
    
    /**
     * 统计用户的问答总数
     * 
     * @param userId 用户ID
     * @return long 该用户的问答总数
     */
    long countByUserId(Long userId);
    
    /**
     * 删除指定时间之前的问答历史
     * 
     * 用于数据清理，删除过期的问答记录
     * 
     * @param beforeTime 指定时间点
     * @return int 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM QaHistory q WHERE q.createTime < :beforeTime")
    int deleteByCreateTimeBefore(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 获取最近的问答记录（用于上下文）
     * 
     * 获取用户最近的几条问答记录，用于维持对话上下文
     * 使用Pageable来实现限制返回记录数
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return List<QaHistory> 最近的问答记录
     */
    @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId ORDER BY q.createTime DESC")
    List<QaHistory> findRecentByUserId(@Param("userId") Long userId, 
                                      org.springframework.data.domain.Pageable pageable);
}
