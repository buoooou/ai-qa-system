package com.ai.qa.user.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    // 暂时注释掉向量数据库数据源配置
    /*
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.vector")
    public DataSource vectorDataSource() {
        return DataSourceBuilder.create().build();
    }
    */
}