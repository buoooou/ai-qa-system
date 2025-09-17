package com.ai.qa.user.infrastructure.repository;

import com.ai.qa.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    
    //@Query("SELECT u FROM User u WHERE u.nick = :nick")
    //User findByNick(@Param("nick") String nick);
    User findByNick(String nick);
}