package com.ai.qa.user.common;

import com.ai.qa.user.api.exception.BusinessException;
import com.ai.qa.user.api.exception.ErrorCode;
import org.springframework.security.core.context.SecurityContextHolder;


public class CommonUtil {
    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        throw new IllegalStateException("Principal is not a valid UserPrincipal");
    }


    public static void assertOwner(Long userId) {
        if (!userId.equals(getCurrentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问该资源");
        }
    }
}
