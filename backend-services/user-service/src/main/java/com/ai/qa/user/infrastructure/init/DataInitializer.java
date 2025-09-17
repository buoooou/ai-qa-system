package com.ai.qa.user.infrastructure.init;

import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化类，在应用启动时创建一些测试数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有用户数据
        if (userRepository.count() == 0) {
            // 创建管理员用户
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setNickname("管理员");
            adminUser.setEmail("admin@example.com");
            adminUser.setStatus(1);
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(adminUser);
            
            // 创建普通用户
            User normalUser = new User();
            normalUser.setUsername("user");
            normalUser.setPassword(passwordEncoder.encode("user123"));
            normalUser.setNickname("普通用户");
            normalUser.setEmail("user@example.com");
            normalUser.setStatus(1);
            normalUser.setCreatedAt(LocalDateTime.now());
            normalUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(normalUser);
            
            System.out.println("测试用户已创建：\n" +
                    "管理员：用户名=admin，密码=admin123\n" +
                    "普通用户：用户名=user，密码=user123");
        } else {
            System.out.println("数据库中已存在用户数据，跳过初始化");
        }
    }
}