package com.ai.qa.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private String secretKeyDev = "mySecretKeyForDevelopmentEnvironment12345";
    private String secretKeyProd = "mySecretKeyForProductionEnvironment67890";
    private boolean production = false;
    
    // Getters and setters
    
    public String getSecretKeyDev() {
        return secretKeyDev;
    }
    
    public void setSecretKeyDev(String secretKeyDev) {
        this.secretKeyDev = secretKeyDev;
    }
    
    public String getSecretKeyProd() {
        return secretKeyProd;
    }
    
    public void setSecretKeyProd(String secretKeyProd) {
        this.secretKeyProd = secretKeyProd;
    }
    
    public boolean isProduction() {
        return production;
    }
    
    public void setProduction(boolean production) {
        this.production = production;
    }
    
    public String getSecretKey() {
        if (production) {
            return secretKeyProd;
        } else {
            return secretKeyDev;
        }
    }
}