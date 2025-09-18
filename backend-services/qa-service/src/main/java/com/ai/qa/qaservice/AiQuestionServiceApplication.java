package com.ai.qa.qaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient // 启用Nacos服务发现
@EnableFeignClients
public class AiQuestionServiceApplication {

    public static void main(String[] args) {
        // 设置不需要代理的主机
        System.setProperty("http.nonProxyHosts",
                "localhost|127.0.0.1|*.internal.com|54.219.180.170");
        System.setProperty("https.nonProxyHosts",
                "localhost|127.0.0.1|*.internal.com|54.219.180.170");

        SpringApplication.run(AiQuestionServiceApplication.class, args);
    }

}
