package com.ai.qa.user;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
    "com.ai.qa.user.api.controller",
    "com.ai.qa.user.application",
    "com.ai.qa.user.infrastructure"
})
@EntityScan("com.ai.qa.user.domain.entity")
@EnableJpaRepositories("com.ai.qa.user.infrastructure.repository")
public class TestApplicationConfig {
}