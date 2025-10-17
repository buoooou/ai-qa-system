package com.ai.qa.service.infrastructure.persistence.repositories;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ai.qa.service.domain.model.QAHistory;
import com.ai.qa.service.domain.repository.QAHistoryRepo;
import com.ai.qa.service.infrastructure.persistence.entities.QAHistoryPO;
import com.ai.qa.service.infrastructure.persistence.mapper.QAHistoryMapper;

import lombok.RequiredArgsConstructor;

/**
 * QA历史记录仓库实现类 实现领域层的QAHistoryRepo接口，提供具体的持久化操作
 * 使用JpaQAHistoryRepository进行数据库操作，使用Mapper进行对象转换
 */
@Component
@RequiredArgsConstructor
public class QAHistoryRepoImpl implements QAHistoryRepo {

    private final JpaQAHistoryRepository jpaQAHistoryRepository;

    private QAHistoryMapper mapper;

    /**
     * 保存QA历史记录
     *
     * @param history 领域对象
     * @return 保存后的领域对象（包含生成的ID）
     */
    @Override
    public QAHistory save(QAHistory history) {
        QAHistoryPO qaHistoryPO = mapper.toPO(history);
        QAHistoryPO savedPO = jpaQAHistoryRepository.save(qaHistoryPO);
        return mapper.toDomain(savedPO);
    }

    /**
     * 根据ID查找QA历史记录
     *
     * @param id 记录ID（字符串格式）
     * @return 包含QA历史记录的Optional对象
     */
    @Override
    public QAHistory findHistoryById(Long id) {
        return jpaQAHistoryRepository.findById(id).map(mapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException());
    }

    @Override
    public QAHistory findById(Long id) {
        return jpaQAHistoryRepository.findById(id).map(mapper::toDomain)
                .orElse(null);
    }

    /**
     * 根据会话ID查找QA历史记录
     *
     * @param sessionId 会话ID
     * @return 该会话的所有QA历史记录列表
     */
    @Override
    public List<QAHistory> findHistoryBySessionId(String sessionId) {
        return jpaQAHistoryRepository.findBySessionId(sessionId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    /**
     * 根据用户ID查找QA历史记录
     *
     * @param userId 用户ID
     * @return 该用户的所有QA历史记录列表
     */
    @Override
    public List<QAHistory> findHistoryByUserId(Long userId) {
        return jpaQAHistoryRepository.findByUserId(userId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    /**
     * 根据用户ID和会话ID查找QA历史记录
     *
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 符合条件的QA历史记录列表
     */
    @Override
    public List<QAHistory> findHistoryByUserIdAndSessionId(Long userId, String sessionId) {
        return jpaQAHistoryRepository.findByUserIdAndSessionId(userId, sessionId).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    /**
     * 删除QA历史记录
     *
     * @param id 要删除的记录ID
     */
    @Override
    public void deleteById(Long id) {
        jpaQAHistoryRepository.deleteById(id);
    }

    @Override
    public long deleteAll(Long userId) {
        return jpaQAHistoryRepository.deleteByUserId(userId);
    }

    @Override
    public long deleteBySessionId(String sessionId) {
        return jpaQAHistoryRepository.deleteBySessionId(sessionId);
    }

    /**
     * 获取用户最近的QA历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 最近的QA历史记录列表
     */
    @Override
    public List<QAHistory> findRecentHistoryByUserId(Long userId, int limit) {
        // 创建分页请求，按时间戳降序排列
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "create_time"));

        // 调用JPA仓库的分页查询方法
        return jpaQAHistoryRepository.findByUserId(userId, pageRequest).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 统计用户的问答数量
     *
     * @param userId 用户ID
     * @return 该用户的问答记录总数
     */
    @Override
    public long countByUserId(Long userId) {
        return jpaQAHistoryRepository.countByUserId(userId);
    }

    @Override
    public List<QAHistory> findTopByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return jpaQAHistoryRepository.findTopByUserId(userId, pageRequest).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QAHistory> searchByKeyword(Long userId, String keyword) {
        return jpaQAHistoryRepository.searchByKeyword(userId, keyword).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
