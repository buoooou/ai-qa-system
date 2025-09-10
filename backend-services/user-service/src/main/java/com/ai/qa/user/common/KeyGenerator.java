package com.ai.qa.user.common;

import io.jsonwebtoken.Jwts;
import java.util.Base64;

/**
 * JWT 密钥生成工具
 * 生成 HS512 算法所需的安全密钥，输出 Base64 编码字符串
 *
 * @author Chen Guoping
 * @version 1.0
 */
public class KeyGenerator {

    /**
     * 生成 512 位 HS512 密钥并打印 Base64 结果
     * 运行方式：java KeyGenerator
     * 输出示例：MzVkMjBjZDctYjUzNi00ZmUyLWEwODAtNGQ5OTZhYzE2M2JhYjY0ZWM0NDctNTIzNS00ZDkyLWExMjMtZjA1M2Q4ZGUxNzRk
     *
     * 安全提示：
     * 1. 妥善保存密钥，禁止硬编码到源码
     * 2. 生产环境建议使用 KMS 等密钥管理服务
     * 3. 不同环境使用不同密钥，定期轮换
     */
    public static void main(String[] args) {
        byte[] keyBytes = Jwts.SIG.HS512.key().build().getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("生成的 512 位密钥（请复制到 application.yml）：");
        System.out.println("==================================================");
        System.out.println(base64Key);
        System.out.println("==================================================");
    }
}