package com.ai.qa.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API网关控制器
 * 
 * 提供网关管理和监控相关的REST接口：
 * 1. 健康检查 - 检查网关服务状态
 * 2. 服务发现 - 查看已注册的微服务
 * 3. 路由信息 - 显示路由配置信息
 * 4. 网关统计 - 显示请求统计信息
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
@RestController
@RequestMapping("/gateway")
public class GatewayController {
    
    /**
     * 服务发现客户端
     * 用于获取注册中心中的服务信息
     */
    @Autowired
    private DiscoveryClient discoveryClient;
    
    /**
     * 网关健康检查
     * 
     * @return Map<String, Object> 健康状态信息
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "API Gateway");
        health.put("version", "1.0");
        health.put("timestamp", System.currentTimeMillis());
        health.put("description", "AI智能问答系统API网关");
        
        return health;
    }
    
    /**
     * 获取已注册的服务列表
     * 
     * @return Map<String, Object> 服务发现信息
     */
    @GetMapping("/services")
    public Map<String, Object> getServices() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取所有服务名称
            List<String> services = discoveryClient.getServices();
            result.put("serviceCount", services.size());
            result.put("services", services);
            
            // 获取每个服务的实例信息
            Map<String, Object> serviceDetails = new HashMap<>();
            for (String serviceName : services) {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("instanceCount", instances.size());
                serviceInfo.put("instances", instances);
                serviceDetails.put(serviceName, serviceInfo);
            }
            result.put("serviceDetails", serviceDetails);
            
            result.put("status", "success");
            result.put("message", "服务发现信息获取成功");
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "获取服务信息失败: " + e.getMessage());
            result.put("serviceCount", 0);
            result.put("services", new String[0]);
        }
        
        return result;
    }
    
    /**
     * 获取路由配置信息
     * 
     * @return Map<String, Object> 路由配置信息
     */
    @GetMapping("/routes")
    public Map<String, Object> getRoutes() {
        Map<String, Object> routes = new HashMap<>();
        
        // 路由配置信息（从application.yml中读取的配置）
        Map<String, Object> routeConfig = new HashMap<>();
        
        // User Service路由
        Map<String, Object> userRoute = new HashMap<>();
        userRoute.put("id", "user-service");
        userRoute.put("uri", "lb://user-service");
        userRoute.put("predicates", new String[]{"Path=/api/user/**", "Path=/api/auth/**"});
        userRoute.put("description", "用户服务路由，处理用户注册、登录、认证等功能");
        
        // QA Service路由
        Map<String, Object> qaRoute = new HashMap<>();
        qaRoute.put("id", "qa-service-yulong");
        qaRoute.put("uri", "lb://qa-service-yulong");
        qaRoute.put("predicates", new String[]{"Path=/api/qa/**"});
        qaRoute.put("description", "问答服务路由，处理AI问答、历史记录等功能");
        
        routeConfig.put("user-service", userRoute);
        routeConfig.put("qa-service-yulong", qaRoute);
        
        routes.put("routes", routeConfig);
        routes.put("routeCount", routeConfig.size());
        routes.put("status", "active");
        routes.put("loadBalancer", "enabled");
        routes.put("description", "Spring Cloud Gateway路由配置");
        
        return routes;
    }
    
    /**
     * 获取网关统计信息
     * 
     * @return Map<String, Object> 统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 基本统计信息
        stats.put("uptime", System.currentTimeMillis());
        stats.put("jvmMemory", getMemoryInfo());
        stats.put("systemInfo", getSystemInfo());
        
        // 网关配置信息
        Map<String, Object> gatewayInfo = new HashMap<>();
        gatewayInfo.put("framework", "Spring Cloud Gateway");
        gatewayInfo.put("version", "3.1.x");
        gatewayInfo.put("reactive", true);
        gatewayInfo.put("features", new String[]{
            "路由转发", "负载均衡", "服务发现", "过滤器链", "限流熔断"
        });
        
        stats.put("gateway", gatewayInfo);
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
    
    /**
     * 获取内存信息
     * 
     * @return Map<String, Object> 内存使用情况
     */
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> memory = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        memory.put("total", totalMemory / 1024 / 1024 + " MB");
        memory.put("used", usedMemory / 1024 / 1024 + " MB");
        memory.put("free", freeMemory / 1024 / 1024 + " MB");
        memory.put("max", maxMemory / 1024 / 1024 + " MB");
        memory.put("usage", String.format("%.2f%%", (double) usedMemory / maxMemory * 100));
        
        return memory;
    }
    
    /**
     * 获取系统信息
     * 
     * @return Map<String, Object> 系统信息
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> system = new HashMap<>();
        
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("processors", Runtime.getRuntime().availableProcessors());
        
        return system;
    }
}
