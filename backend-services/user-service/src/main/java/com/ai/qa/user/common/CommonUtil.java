package com.ai.qa.user.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {
    
    /**
     * 使用SHA-256算法对密码进行加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
}