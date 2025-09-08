package com.ai.qa.user.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 日期时间工具类
 * 
 * 提供用户服务中常用的日期时间处理方法：
 * 1. 日期格式化
 * 2. 日期解析
 * 3. 日期计算
 * 4. 时间比较
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
public class DateUtil {
    
    /**
     * 标准日期时间格式
     */
    public static final String STANDARD_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 标准日期格式
     */
    public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd";
    
    /**
     * 标准时间格式
     */
    public static final String STANDARD_TIME_PATTERN = "HH:mm:ss";
    
    /**
     * ISO日期时间格式
     */
    public static final String ISO_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    
    /**
     * 标准日期时间格式化器
     */
    public static final DateTimeFormatter STANDARD_DATETIME_FORMATTER = 
        DateTimeFormatter.ofPattern(STANDARD_DATETIME_PATTERN);
    
    /**
     * 标准日期格式化器
     */
    public static final DateTimeFormatter STANDARD_DATE_FORMATTER = 
        DateTimeFormatter.ofPattern(STANDARD_DATE_PATTERN);
    
    /**
     * 标准时间格式化器
     */
    public static final DateTimeFormatter STANDARD_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern(STANDARD_TIME_PATTERN);
    
    /**
     * ISO日期时间格式化器
     */
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = 
        DateTimeFormatter.ofPattern(ISO_DATETIME_PATTERN);
    
    /**
     * 私有构造函数，防止实例化
     */
    private DateUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * 获取当前日期时间
     * 
     * @return LocalDateTime 当前日期时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * 格式化日期时间为标准格式字符串
     * 
     * @param dateTime 日期时间对象
     * @return String 格式化后的字符串（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(STANDARD_DATETIME_FORMATTER);
    }
    
    /**
     * 格式化日期时间为指定格式字符串
     * 
     * @param dateTime 日期时间对象
     * @param pattern 格式模式
     * @return String 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化日期为标准格式字符串
     * 
     * @param dateTime 日期时间对象
     * @return String 格式化后的日期字符串（yyyy-MM-dd）
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(STANDARD_DATE_FORMATTER);
    }
    
    /**
     * 格式化时间为标准格式字符串
     * 
     * @param dateTime 日期时间对象
     * @return String 格式化后的时间字符串（HH:mm:ss）
     */
    public static String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(STANDARD_TIME_FORMATTER);
    }
    
    /**
     * 解析标准格式的日期时间字符串
     * 
     * @param dateTimeStr 日期时间字符串（yyyy-MM-dd HH:mm:ss）
     * @return LocalDateTime 解析后的日期时间对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), STANDARD_DATETIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 解析指定格式的日期时间字符串
     * 
     * @param dateTimeStr 日期时间字符串
     * @param pattern 格式模式
     * @return LocalDateTime 解析后的日期时间对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty() || pattern == null) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateTimeStr.trim(), DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 计算两个日期时间之间的天数差
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return long 天数差（正数表示end在start之后）
     */
    public static long daysBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
    }
    
    /**
     * 计算两个日期时间之间的小时数差
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return long 小时数差（正数表示end在start之后）
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的分钟数差
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return long 分钟数差（正数表示end在start之后）
     */
    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(start, end);
    }
    
    /**
     * 计算两个日期时间之间的秒数差
     * 
     * @param start 开始日期时间
     * @param end 结束日期时间
     * @return long 秒数差（正数表示end在start之后）
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.SECONDS.between(start, end);
    }
    
    /**
     * 判断日期时间是否在指定范围内
     * 
     * @param dateTime 待判断的日期时间
     * @param start 开始时间
     * @param end 结束时间
     * @return boolean true-在范围内，false-不在范围内
     */
    public static boolean isBetween(LocalDateTime dateTime, LocalDateTime start, LocalDateTime end) {
        if (dateTime == null || start == null || end == null) {
            return false;
        }
        return !dateTime.isBefore(start) && !dateTime.isAfter(end);
    }
    
    /**
     * 判断日期时间是否是今天
     * 
     * @param dateTime 日期时间
     * @return boolean true-是今天，false-不是今天
     */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    /**
     * 获取友好的时间描述
     * 
     * 例如：刚刚、1分钟前、1小时前、1天前等
     * 
     * @param dateTime 日期时间
     * @return String 友好的时间描述
     */
    public static String getFriendlyTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "未知时间";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long seconds = secondsBetween(dateTime, now);
        
        if (seconds < 0) {
            return "未来时间";
        } else if (seconds < 60) {
            return "刚刚";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "分钟前";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + "小时前";
        } else if (seconds < 2592000) {
            long days = seconds / 86400;
            return days + "天前";
        } else {
            return formatDate(dateTime);
        }
    }
}
