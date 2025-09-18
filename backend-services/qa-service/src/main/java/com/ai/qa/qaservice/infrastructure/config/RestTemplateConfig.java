package com.ai.qa.qaservice.infrastructure.config;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

// 修改 RestTemplateConfig.java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        // 配置代理（如果需要）
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("9.36.235.13", 8080));
        requestFactory.setProxy(proxy);

        // 增加超时设置，避免无限等待
        requestFactory.setConnectTimeout(60000); // 连接超时60秒
        requestFactory.setReadTimeout(90000); // 读取超时90秒

        return new RestTemplate(requestFactory);
    }
}
