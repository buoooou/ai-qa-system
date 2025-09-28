package com.ai.qa.user.common;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtil {

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_LOCAL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);

    /**
     * 获取当前日期字符串（默认格式：yyyy-MM-dd）
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DEFAULT_LOCAL_DATETIME_FORMATTER);
    }

    /**
     * 获取当前日期时间字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DEFAULT_LOCAL_DATETIME_FORMATTER);
    }

    /**
     * 格式化LocalDate为字符串
     */
    public static String formatLocalDate(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 格式化LocalDateTime为字符串
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, String format) {
        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 解析日期字符串为LocalDate
     */
    public static LocalDate parseLocalDate(String dateStr, String format) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 解析日期时间字符串为LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr, String format) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 在指定日期上增加天数
     */
    public static LocalDate addDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * 判断日期是否为今天
     */
    public static boolean isToday(LocalDate date) {
        return date.isEqual(LocalDate.now());
    }

    /**
     * 判断日期时间是否为今天
     */
    public static boolean isToday(LocalDateTime dateTime) {
        return dateTime.toLocalDate().isEqual(LocalDate.now());
    }

    /**
     * 解析字符串为LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_LOCAL_DATETIME_FORMATTER);
    }
}
