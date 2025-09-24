package com.ai.qa.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 数据库连接测试工具
 * 在应用启动时自动执行数据库连接测试
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        // 生成一个唯一的测试表名
        String testTableName = "test_connection_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        try {
            System.out.println("\n===== 开始测试数据库连接 =====");
            System.out.println("数据库地址: 3.84.225.222:3306");
            
            // 创建测试表
            jdbcTemplate.execute("CREATE TABLE " + testTableName + " (id INT PRIMARY KEY, name VARCHAR(50))");
            System.out.println("✅ 成功创建测试表: " + testTableName);
            
            // 插入测试数据
            jdbcTemplate.update("INSERT INTO " + testTableName + " (id, name) VALUES (1, '测试数据')");
            System.out.println("✅ 成功插入测试数据");
            
            // 查询验证
            List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM " + testTableName);
            System.out.println("✅ 查询结果: " + results);
            
            // 清理测试表
            jdbcTemplate.execute("DROP TABLE " + testTableName);
            System.out.println("✅ 成功清理测试表");
            
            System.out.println("🎉 数据库连接测试成功!");
            System.out.println("===== 测试完成 =====\n");
        } catch (Exception e) {
            System.err.println("❌ 数据库测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}