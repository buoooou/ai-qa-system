package com.ai.qa.user.infrastructure.repository;

import com.ai.qa.user.domain.entity.User;
import com.ai.qa.user.domain.repository.UserRepository;
import com.ai.qa.user.domain.valueobject.UserVectorEmbedding;
import com.ai.qa.user.infrastructure.adapter.VectorStoreAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final UserRepositoryJPA userRepositoryJPA;
    private final VectorStoreAdapter vectorStoreAdapter;
    
    @Override
    public Optional<User> findByUserName(String userName) {
        return userRepositoryJPA.findByUserName(userName);
    }
    
    @Override
    public boolean existsByUserName(String userName) {
        return userRepositoryJPA.existsByUserName(userName);
    }
    
    @Override
    public List<User> findSimilarUsersByVector(UserVectorEmbedding vectorEmbedding, int limit) {
        // 这里需要实现基于向量的相似用户查找
        // 由于JPA不直接支持向量操作，这里暂时返回空列表
        // 实际实现需要使用vectorStoreAdapter或原生SQL查询
        return List.of();
    }
    
    @Override
    public void saveVectorEmbedding(Long userId, UserVectorEmbedding vectorEmbedding) {
        Optional<User> userOpt = userRepositoryJPA.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateVectorEmbedding(vectorEmbedding);
            userRepositoryJPA.save(user);
            log.info("Saved vector embedding for user: {}", userId);
        } else {
            log.warn("User not found when saving vector embedding: {}", userId);
        }
    }
    
    @Override
    public Optional<UserVectorEmbedding> findVectorEmbeddingByUserId(Long userId) {
        Optional<User> userOpt = userRepositoryJPA.findById(userId);
        return userOpt.map(User::getVectorEmbedding);
    }
    
    @Override
    public User save(User user) {
        return userRepositoryJPA.save(user);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userRepositoryJPA.findById(id);
    }
    
    @Override
    public List<User> findAll() {
        return userRepositoryJPA.findAll();
    }
    
    @Override
    public void deleteById(Long id) {
        userRepositoryJPA.deleteById(id);
    }
    
    @Override
    public void delete(User user) {
        userRepositoryJPA.delete(user);
    }
    
    // 实现JpaRepository的其他方法
    @Override
    public List<User> findAll(Sort sort) {
        return userRepositoryJPA.findAll(sort);
    }
    
    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepositoryJPA.findAll(pageable);
    }
    
    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        return userRepositoryJPA.findAllById(ids);
    }
    
    @Override
    public long count() {
        return userRepositoryJPA.count();
    }
    
    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        userRepositoryJPA.deleteAllById(ids);
    }
    
    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        userRepositoryJPA.deleteAll(entities);
    }
    
    @Override
    public void deleteAll() {
        userRepositoryJPA.deleteAll();
    }
    
    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        return userRepositoryJPA.saveAll(entities);
    }
    
    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return userRepositoryJPA.findOne(example);
    }
    
    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return userRepositoryJPA.findAll(example);
    }
    
    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return userRepositoryJPA.findAll(example, sort);
    }
    
    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        return userRepositoryJPA.findAll(example, pageable);
    }
    
    @Override
    public <S extends User> long count(Example<S> example) {
        return userRepositoryJPA.count(example);
    }
    
    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return userRepositoryJPA.exists(example);
    }
    
    @Override
    public void flush() {
        userRepositoryJPA.flush();
    }
    
    @Override
    public <S extends User> S saveAndFlush(S entity) {
        return userRepositoryJPA.saveAndFlush(entity);
    }
    
    @Override
    public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
        return userRepositoryJPA.saveAllAndFlush(entities);
    }
    
    @Override
    public void deleteAllInBatch(Iterable<User> entities) {
        userRepositoryJPA.deleteAllInBatch(entities);
    }
    
    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        userRepositoryJPA.deleteAllByIdInBatch(ids);
    }
    
    @Override
    public void deleteAllInBatch() {
        userRepositoryJPA.deleteAllInBatch();
    }
    
    @Override
    public User getOne(Long aLong) {
        return userRepositoryJPA.getOne(aLong);
    }
    
    @Override
    public User getById(Long aLong) {
        return userRepositoryJPA.getById(aLong);
    }
    
    @Override
    public User getReferenceById(Long aLong) {
        return userRepositoryJPA.getReferenceById(aLong);
    }
    
    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return userRepositoryJPA.findBy(example, queryFunction);
    }
    
    /**
     * JPA仓库接口
     */
    public interface UserRepositoryJPA extends org.springframework.data.jpa.repository.JpaRepository<User, Long> {
        Optional<User> findByUserName(String userName);
        boolean existsByUserName(String userName);
    }
}