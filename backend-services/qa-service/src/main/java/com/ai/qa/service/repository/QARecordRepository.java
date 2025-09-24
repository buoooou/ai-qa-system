package com.ai.qa.service.repository;

import com.ai.qa.service.entity.QARecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QARecordRepository extends JpaRepository<QARecord, Long> {

    /**
     * 根据用户ID分页查询问答记录
     */
    Page<QARecord> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据会话ID查询问答记录
     */
    List<QARecord> findBySessionIdOrderByCreateTimeAsc(String sessionId);

    /**
     * 根据用户ID和问题类型查询
     */
    List<QARecord> findByUserIdAndQuestionTypeOrderByCreateTimeDesc(Long userId, String questionType);

    /**
     * 统计用户问答总数
     */
    long countByUserId(Long userId);

    /**
     * 查询用户最近的问答记录
     */
    @Query("SELECT q FROM QARecord q WHERE q.userId = :userId ORDER BY q.createTime DESC")
    List<QARecord> findRecentQuestionsByUserId(@Param("userId") Long userId, Pageable pageable);
}
