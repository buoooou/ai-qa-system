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
        * 按用户ID查询最近的问答记录，按创建时间降序排列
        * 
        * @param userId 用户ID
        * @return 符合条件的问答记录列表
        */
       @Query("SELECT q FROM QaHistory q WHERE q.userId = :userId ORDER BY createTime DESC")
       List<QaHistory> findByUserIdOrderByCreateTimeDesc(Long userId);

       /**
        * 删除指定用户的问答记录
        * 
        * @param userId 用户ID
        */
       @Modifying // 添加此注解标识为修改操作
       @Transactional // 添加事务支持
       @Query("DELETE FROM QaHistory q WHERE q.userId = :userId")
       int deleteByUserId(Long userId);
}
