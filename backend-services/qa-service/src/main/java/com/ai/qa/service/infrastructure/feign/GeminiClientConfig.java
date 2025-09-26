package com.ai.qa.service.infrastructure.feign;

import feign.RequestInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import feign.httpclient.ApacheHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GeminiClientConfig {

    @Bean
    RequestInterceptor requestInterceptor() {
        String apiKey = System.getenv("GOOGLE_API_KEY");
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + apiKey);
            requestTemplate.header("Content-Type", "application/json");
        };
    }

    @Bean
    public ApacheHttpClient feignClient() {
        HttpHost proxy = new HttpHost("proxy.emea.ibm.com", 8080, "http");
        CloseableHttpClient httpClient = HttpClientBuilder.create().setProxy(proxy).build();
        return new ApacheHttpClient(httpClient);
    }
}
