package com.ai.qa.user.common;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public class CommonUtil {

    /**
     * 邮箱格式验证正则表达式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    /**
     * 用户名格式验证正则表达式（4-20位字母数字下划线）
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    
    /**
     * 密码强度验证正则表达式（至少8位，包含字母和数字）
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");
    
    /**
     * 安全随机数生成器
     */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /**
     * 私有构造函数，防止实例化
     */
    private CommonUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * 验证邮箱格式是否正确
     * 
     * @param email 邮箱地址
     * @return boolean true-格式正确，false-格式错误
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * 验证用户名格式是否正确
     * 
     * @param username 用户名
     * @return boolean true-格式正确，false-格式错误
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * 验证密码强度是否符合要求
     * 
     * @param password 密码
     * @return boolean true-强度符合要求，false-强度不够
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 检查字符串是否为空或仅包含空白字符
     * 
     * @param str 待检查的字符串
     * @return boolean true-为空或空白，false-不为空
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 检查字符串是否不为空且不仅包含空白字符
     * 
     * @param str 待检查的字符串
     * @return boolean true-不为空，false-为空或空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * 生成指定长度的随机字符串
     * 
     * @param length 字符串长度
     * @return String 随机字符串
     */
    public static String generateRandomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(SECURE_RANDOM.nextInt(characters.length())));
        }
        
        return result.toString();
    }
    
    /**
     * 生成随机数字字符串
     * 
     * @param length 字符串长度
     * @return String 随机数字字符串
     */
    public static String generateRandomNumbers(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(SECURE_RANDOM.nextInt(10));
        }
        
        return result.toString();
    }
    
    /**
     * 脱敏邮箱地址
     * 
     * 将邮箱地址中间部分用*号替换，保护用户隐私
     * 例如：test@example.com -> t***@example.com
     * 
     * @param email 原始邮箱地址
     * @return String 脱敏后的邮箱地址
     */
    public static String maskEmail(String email) {
        if (isBlank(email) || !isValidEmail(email)) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "*" + domainPart;
        } else {
            return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domainPart;
        }
    }
    
    /**
     * 脱敏用户名
     * 
     * 将用户名中间部分用*号替换
     * 例如：username -> use***me
     * 
     * @param username 原始用户名
     * @return String 脱敏后的用户名
     */
    public static String maskUsername(String username) {
        if (isBlank(username)) {
            return username;
        }
        
        if (username.length() <= 2) {
            return username;
        } else if (username.length() <= 4) {
            return username.charAt(0) + "*" + username.charAt(username.length() - 1);
        } else {
            return username.substring(0, 2) + "***" + username.substring(username.length() - 2);
        }
    }
    
    /**
     * 安全地转换字符串为Long类型
     * 
     * @param str 字符串
     * @param defaultValue 默认值
     * @return Long 转换结果
     */
    public static Long safeParseLong(String str, Long defaultValue) {
        if (isBlank(str)) {
            return defaultValue;
        }
        
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 安全地转换字符串为Integer类型
     * 
     * @param str 字符串
     * @param defaultValue 默认值
     * @return Integer 转换结果
     */
    public static Integer safeParseInt(String str, Integer defaultValue) {
        if (isBlank(str)) {
            return defaultValue;
        }
        
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
