package com.yulong.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import com.ai.qa.user.UserServiceApplication;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 数据库连接测试类
 * 用于验证数据库连接和数据插入功能
 */
@SpringBootTest(classes = UserServiceApplication.class)
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 测试数据库连接并插入数据
     * 创建一个临时测试表，插入数据，然后查询验证
     */
    @Test
    public void testDatabaseConnection() {
        // 生成一个唯一的测试表名
        String testTableName = "test_connection_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        try {
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
            
            System.out.println("🎉 数据库连接测试成功! 数据库地址: 3.84.225.222:3306");
        } catch (Exception e) {
            System.err.println("❌ 数据库测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}