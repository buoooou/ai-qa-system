package com.ai.qa.user.infrastructure.persistence.repositories;


import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {
    // Spring Data JPA 会自动实现 JpaRepository 中的所有方法，
    // 包括我们UserRepository中定义的 findById 和 save 方法，因为它们的方法签名是匹配的。
    // 因此，这里不需要写任何代码。
}
