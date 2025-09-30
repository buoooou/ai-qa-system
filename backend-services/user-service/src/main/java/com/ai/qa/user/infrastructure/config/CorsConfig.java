package com.ai.qa.user.infrastructure.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
 @Bean
 public CorsFilter corsFilter() {
	    CorsConfiguration config = new CorsConfiguration();
        // 允许前端的域名（这里是 localhost:3000）
        config.addAllowedOrigin("http://16.176.136.101:3000");
	    config.setAllowedOriginPatterns(List.of("*"));
	    config.addAllowedHeader("*");
	    config.addAllowedMethod("*");
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
 }
}
