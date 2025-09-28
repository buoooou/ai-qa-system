package com.ai.qa.user.common;

import java.util.*;
import java.util.regex.Pattern;
import java.security.MessageDigest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * 通用工具类，提供字符串、集合、对象、校验、加密、随机等常用工具方法
 */
public class CommonUtil {

    /**
     * 判断字符串是否为null或空字符串（去空格后）
     * @param str 待检查的字符串
     * @return true: 空或null, false: 非空
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 对字符串的指定区间进行脱敏处理
     * @param str 原始字符串
     * @param start 脱敏起始位置（包含）
     * @param end 脱敏结束位置（不包含）
     * @param maskChar 脱敏替换字符
     * @return 脱敏后的字符串，如果参数不合法则返回原字符串
     */
    public static String maskSensitiveInfo(String str, int start, int end, char maskChar) {
        if (isBlank(str) || start < 0 || end > str.length() || start >= end) {
            return str;
        }
        char[] chars = str.toCharArray();
        Arrays.fill(chars, start, end, maskChar);
        return new String(chars);
    }

    /**
     * 判断集合是否为null或空集合
     * @param collection 待检查的集合
     * @return true: 空或null, false: 非空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 将数组转换为List集合
     * @param array 源数组
     * @return 转换后的List，如果数组为null则返回空List
     */
    public static <T> List<T> toList(T[] array) {
        return array == null ? new ArrayList<>() : Arrays.asList(array);
    }

    /**
     * 判断对象是否为null
     * @param obj 待检查的对象
     * @return true: 为null, false: 非null
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 对象深拷贝（基于序列化实现，对象需实现Serializable接口）
     * @param obj 待拷贝对象
     * @return 拷贝后的对象
     * @throws RuntimeException 如果对象不可序列化或拷贝失败
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T obj) {
        if (!(obj instanceof Serializable)) {
            throw new RuntimeException("深拷贝失败：对象未实现Serializable接口");
        }
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(obj);
            oos.flush();
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("深拷贝失败", e);
        }
    }

    // 正则表达式预编译
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$");

    /**
     * 校验手机号格式（中国大陆）
     * @param phone 待校验的手机号
     * @return true: 格式正确, false: 格式错误
     */
    public static boolean isValidPhone(String phone) {
        return !isBlank(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 校验邮箱格式
     * @param email 待校验的邮箱
     * @return true: 格式正确, false: 格式错误
     */
    public static boolean isValidEmail(String email) {
        return !isBlank(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * MD5加密（32位小写）
     * @param input 原始字符串
     * @return 加密后的字符串
     * @throws RuntimeException 加密失败时抛出
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * Base64编码
     * @param input 原始字符串
     * @return 编码后的字符串
     */
    public static String base64Encode(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     * @param input 编码字符串
     * @return 解码后的原始字符串
     */
    public static String base64Decode(String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }

    /**
     * 生成UUID（无横线）
     * @return 32位UUID字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成随机字符串（字母数字组合）
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成默认用户昵称（格式：用户+6位随机数字）
     * @return 生成的昵称（如「用户123456」）
     */
    /**
     * 生成xxxx-xxxx格式的默认昵称（x为数字或大小写字母）
     * @return 生成的昵称（如"aB3D-9fK2"）
     */
    public static String generateDefaultNick() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
