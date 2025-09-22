package com.ai.qa.user.application.service;



import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrorCode;
import com.ai.qa.user.domain.model.User;
import com.ai.qa.user.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;


@Service
public class UserApplicationService {

    private final UserRepository userRepository;

    @Autowired
    public UserApplicationService(UserRepository userRepository) {
        // 注意：这里注入的是我们在领域层定义的接口，而不是具体的实现
        this.userRepository = userRepository;
    }

    /**
     * 更新用户昵称的应用服务方法
     * @param userId 目标用户的ID
     * @param newNickname 新的昵称
     * @return 更新后的User对象
     */
    @Transactional // 保证操作的原子性
    public User updateNickname(Long userId, String newNickname) {
        // 1. 从仓库加载聚合根
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 调用聚合根的业务方法来执行操作
        //    所有的业务规则都在User.changeNickname()方法内部执行
        user.changeNickname(newNickname);

        // 3. 将变更后的聚合根交由仓库进行持久化
        return userRepository.save(user);
    }
}