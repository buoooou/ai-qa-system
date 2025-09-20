package com.ai.qa.qaservice.domain.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ai.qa.qaservice.domain.entity.QaHistory;

import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface QaHistoryRepository extends JpaRepository<QaHistory, Long> {

       /**
        * 按用户ID查询最近的问答记录，按创建时间升序排列
        * 
        * @param userId 用户ID
        * @return 符合条件的问答记录列表
        */
       @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId ORDER BY createTime ASC")
       List<QaHistory> findByUserIdOrderByCreateTimeAsc(Long userId);

       /**
        * 删除指定用户的问答记录
        * 
        * @param userId 用户ID
        */
       @Modifying // 添加此注解标识为修改操作
       @Transactional // 添加事务支持
       @Query("DELETE FROM QaHistory q WHERE q.userId = :userId")
       int deleteByUserId(Long userId);

       /**
        * 按用户ID和对话ID查询问答记录，按创建时间升序排列（用于上下文拼接）
        */
       @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId AND q.conversationId = :conversationId ORDER BY createTime ASC")
       List<QaHistory> findByUserIdAndConversationIdOrderByCreateTimeAsc(Long userId, String conversationId);

       /**
        * 按用户ID和对话ID删除记录
        */
       @Modifying
       @Transactional
       @Query("DELETE FROM QaHistory q WHERE q.userId = :userId AND q.conversationId = :conversationId")
       int deleteByUserIdAndConversationId(Long userId, String conversationId);

       /**
        * 按用户ID查询所有对话ID
        */
       @Query("SELECT q.conversationId FROM QaHistory q WHERE q.userId = :userId ORDER BY q.createTime DESC")
       List<String> findConversationIdsByUserId(Long userId);
}
