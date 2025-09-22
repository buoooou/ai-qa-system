package com.ai.qa.service.controller;

import com.ai.qa.service.client.UserServiceClient;
import com.ai.qa.service.dto.ApiResponse;
import com.ai.qa.service.dto.QaRequest;
import com.ai.qa.service.dto.QaResponse;
import com.ai.qa.service.dto.UserInfoDto;
import com.ai.qa.service.service.IQaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 问答控制器
 * 
 * 提供问答相关的REST API接口：
 * 1. POST /api/qa/ask - 提交问题并获取AI回答
 * 2. GET /api/qa/history/{userId} - 获取用户问答历史
 * 3. GET /api/qa/history/{userId}/paged - 分页获取用户问答历史
 * 4. GET /api/qa/search/{userId} - 搜索用户问答历史
 * 5. GET /api/qa/{id} - 根据ID获取问答记录
 * 6. GET /api/qa/count/{userId} - 获取用户问答总数
 * 
 * @author Qiao Zhe
 * @version 1.0
 * @since 2025-09-06
 */
@Tag(name = "问答服务", description = "AI智能问答相关接口")
@Slf4j                      // Lombok注解：自动生成日志对象
@RestController             // Spring注解：标识这是一个REST控制器
@RequestMapping("/api/qa")   // 设置基础路径
@RequiredArgsConstructor    // Lombok注解：为final字段生成构造函数
@Validated                  // 启用方法级别的参数验证
public class QaController {
    
    /**
     * 问答服务层接口
     * 
     * 依赖抽象而不是具体实现，遵循依赖倒置原则
     * Spring会自动注入IQaService的实现类（QaService）
     */
    private final IQaService qaService;
    
    /**
     * 用户服务客户端
     * 
     * 通过Feign Client调用user-service
     */
    private final UserServiceClient userServiceClient;
    
    /**
     * 提交问题并获取AI回答
     * 
     * 这是核心接口，接收用户问题，调用AI服务获取回答
     * 
     * @param request 问答请求，包含用户ID、问题内容等
     * @return ApiResponse<QaResponse> 问答结果
     */
    @Operation(summary = "提交问题获取AI回答", description = "向AI提交问题并获取智能回答，支持上下文对话")
    @PostMapping("/ask")
    public ApiResponse<QaResponse> askQuestion(@Valid @RequestBody QaRequest request) {
        log.info("收到问答请求，用户ID: {}, 问题长度: {}", 
                request.getUserId(), request.getQuestion().length());
        
        try {
            // 调用服务层处理问答
            QaResponse response = qaService.askQuestion(request);
            
            log.info("问答处理成功，用户ID: {}, 回答长度: {}", 
                    request.getUserId(), response.getAnswer().length());
            
            return ApiResponse.success("问答成功", response);
            
        } catch (RuntimeException e) {
            log.error("问答处理失败，用户ID: {}, 错误信息: {}", 
                     request.getUserId(), e.getMessage());
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 获取用户问答历史
     * 
     * 返回指定用户的所有问答记录，按时间倒序排列
     * 
     * @param userId 用户ID
     * @return ApiResponse<List<QaResponse>> 问答历史列表
     */
    @Operation(summary = "获取用户问答历史", description = "获取指定用户的所有问答记录，按时间倒序排列")
    @GetMapping("/history/{userId}")
    public ApiResponse<List<QaResponse>> getUserHistory(
            @Parameter(description = "用户ID", required = true) @PathVariable @NotNull @Min(1) Long userId) {
        log.info("收到查询用户问答历史请求，用户ID: {}", userId);
        
        try {
            List<QaResponse> histories = qaService.getUserHistory(userId);
            
            log.info("查询用户问答历史成功，用户ID: {}, 记录数: {}", userId, histories.size());
            return ApiResponse.success("查询成功", histories);
            
        } catch (Exception e) {
            log.error("查询用户问答历史失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页获取用户问答历史
     * 
     * 支持分页查询，避免一次性返回过多数据
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始，默认0）
     * @param size 每页大小（默认10，最大100）
     * @return ApiResponse<List<QaResponse>> 分页的问答历史列表
     */
    @GetMapping("/history/{userId}/paged")
    public ApiResponse<List<QaResponse>> getUserHistoryPaged(
            @PathVariable @NotNull @Min(1) Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        
        log.info("收到分页查询用户问答历史请求，用户ID: {}, 页码: {}, 每页大小: {}", 
                userId, page, size);
        
        // 限制每页最大大小，防止查询过多数据
        if (size > 100) {
            size = 100;
            log.warn("每页大小超过限制，已调整为100，用户ID: {}", userId);
        }
        
        try {
            List<QaResponse> histories = qaService.getUserHistoryPaged(userId, page, size);
            
            log.info("分页查询用户问答历史成功，用户ID: {}, 返回记录数: {}", userId, histories.size());
            return ApiResponse.success("查询成功", histories);
            
        } catch (Exception e) {
            log.error("分页查询用户问答历史失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 搜索用户问答历史
     * 
     * 根据关键词在用户的问答历史中搜索匹配的记录
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return ApiResponse<List<QaResponse>> 匹配的问答历史
     */
    @GetMapping("/search/{userId}")
    public ApiResponse<List<QaResponse>> searchUserHistory(
            @PathVariable @NotNull @Min(1) Long userId,
            @RequestParam @NotBlank String keyword) {
        
        log.info("收到搜索用户问答历史请求，用户ID: {}, 关键词: {}", userId, keyword);
        
        try {
            List<QaResponse> histories = qaService.searchUserHistory(userId, keyword);
            
            log.info("搜索用户问答历史成功，用户ID: {}, 关键词: {}, 匹配记录数: {}", 
                    userId, keyword, histories.size());
            
            return ApiResponse.success("搜索成功", histories);
            
        } catch (Exception e) {
            log.error("搜索用户问答历史失败，用户ID: {}, 关键词: {}, 错误信息: {}", 
                     userId, keyword, e.getMessage());
            return ApiResponse.error("搜索失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取问答记录
     * 
     * 获取指定ID的问答记录详情
     * 
     * @param id 问答记录ID
     * @return ApiResponse<QaResponse> 问答记录
     */
    @GetMapping("/{id}")
    public ApiResponse<QaResponse> getQaById(@PathVariable @NotNull @Min(1) Long id) {
        log.debug("收到根据ID查询问答记录请求，ID: {}", id);
        
        try {
            QaResponse response = qaService.getQaById(id);
            
            if (response != null) {
                log.debug("根据ID查询问答记录成功，ID: {}", id);
                return ApiResponse.success("查询成功", response);
            } else {
                log.warn("问答记录不存在，ID: {}", id);
                return ApiResponse.error("问答记录不存在");
            }
            
        } catch (Exception e) {
            log.error("根据ID查询问答记录失败，ID: {}, 错误信息: {}", id, e.getMessage());
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户问答总数
     * 
     * 统计指定用户的问答记录总数
     * 
     * @param userId 用户ID
     * @return ApiResponse<Long> 问答总数
     */
    @GetMapping("/count/{userId}")
    public ApiResponse<Long> getUserQaCount(@PathVariable @NotNull @Min(1) Long userId) {
        log.debug("收到查询用户问答总数请求，用户ID: {}", userId);
        
        try {
            long count = qaService.getUserQaCount(userId);
            
            log.debug("查询用户问答总数成功，用户ID: {}, 总数: {}", userId, count);
            return ApiResponse.success("查询成功", count);
            
        } catch (Exception e) {
            log.error("查询用户问答总数失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     * 
     * 用于检查问答服务是否正常运行
     * 
     * @return ApiResponse<String> 服务状态
     */
    @Operation(summary = "健康检查", description = "检查问答服务是否正常运行")
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("QA Service is running");
    }
    
    /**
     * 删除问答记录
     * 
     * 根据记录ID删除指定的问答记录，需要验证用户权限
     * 
     * @param id 问答记录ID
     * @param userId 用户ID（用于权限验证）
     * @return ApiResponse<Boolean> 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteQaRecord(
            @PathVariable @NotNull @Min(1) Long id,
            @RequestParam @NotNull @Min(1) Long userId) {
        
        log.info("收到删除问答记录请求，记录ID: {}, 用户ID: {}", id, userId);
        
        try {
            boolean deleted = qaService.deleteQaRecord(id, userId);
            
            if (deleted) {
                log.info("删除问答记录成功，记录ID: {}", id);
                return ApiResponse.success("删除成功", true);
            } else {
                log.warn("删除问答记录失败，记录ID: {}", id);
                return ApiResponse.error("删除失败：记录不存在或无权限");
            }
            
        } catch (Exception e) {
            log.error("删除问答记录异常，记录ID: {}, 错误信息: {}", id, e.getMessage());
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量删除用户问答记录
     * 
     * 删除指定用户的所有问答记录
     * 
     * @param userId 用户ID
     * @return ApiResponse<Integer> 删除的记录数量
     */
    @DeleteMapping("/user/{userId}/all")
    public ApiResponse<Integer> deleteUserAllQaRecords(
            @PathVariable @NotNull @Min(1) Long userId) {
        
        log.info("收到批量删除用户问答记录请求，用户ID: {}", userId);
        
        try {
            int deletedCount = qaService.deleteUserAllQaRecords(userId);
            
            log.info("批量删除用户问答记录成功，用户ID: {}, 删除数量: {}", userId, deletedCount);
            return ApiResponse.success("批量删除成功", deletedCount);
            
        } catch (Exception e) {
            log.error("批量删除用户问答记录失败，用户ID: {}, 错误信息: {}", userId, e.getMessage());
            return ApiResponse.error("批量删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取服务信息
     * 
     * 返回问答服务的基本信息
     * 
     * @return ApiResponse<Map<String, Object>> 服务信息
     */
    @GetMapping("/info")
    public ApiResponse<java.util.Map<String, Object>> getServiceInfo() {
        java.util.Map<String, Object> info = new java.util.HashMap<>();
        info.put("service", "QA Service");
        info.put("version", "1.0");
        info.put("description", "AI智能问答服务");
        info.put("features", java.util.Arrays.asList(
            "AI问答", "历史记录", "上下文对话", "搜索功能", "记录删除", "Feign Client服务间调用"
        ));
        
        return ApiResponse.success("获取服务信息成功", info);
    }
    
    /**
     * 测试Feign Client - 获取用户信息
     * 
     * 通过Feign Client调用user-service获取用户信息
     * 用于测试服务间通信是否正常
     * 
     * @param userId 用户ID
     * @return ApiResponse<UserInfoDto> 用户信息
     */
    @GetMapping("/test/user/{userId}")
    public ApiResponse<UserInfoDto> testGetUserInfo(@PathVariable @NotNull @Min(1) Long userId) {
        log.info("测试Feign Client获取用户信息，用户ID: {}", userId);
        
        try {
            ApiResponse<UserInfoDto> response = userServiceClient.getUserById(userId);
            
            if (response != null && response.getCode() == 200 && response.getData() != null) {
                log.info("Feign Client调用成功，用户ID: {}, 用户名: {}", 
                        userId, response.getData().getUserName());
                return ApiResponse.success("Feign Client调用成功", response.getData());
            } else {
                log.warn("Feign Client调用失败，用户ID: {}, 响应: {}", userId, response);
                return ApiResponse.error("用户不存在或服务调用失败");
            }
            
        } catch (Exception e) {
            log.error("Feign Client调用异常，用户ID: {}, 错误信息: {}", userId, e.getMessage(), e);
            return ApiResponse.error("服务间调用失败：" + e.getMessage());
        }
    }
    
    /**
     * 测试Feign Client - 检查用户服务健康状态
     * 
     * 通过Feign Client调用user-service的健康检查接口
     * 
     * @return ApiResponse<String> 健康检查结果
     */
    @GetMapping("/test/user-service/health")
    public ApiResponse<String> testUserServiceHealth() {
        log.info("测试Feign Client调用用户服务健康检查");
        
        try {
            ApiResponse<String> response = userServiceClient.checkHealth();
            
            if (response != null && response.getCode() == 200) {
                log.info("用户服务健康检查成功，响应: {}", response.getMessage());
                return ApiResponse.success("用户服务健康检查成功", response.getData());
            } else {
                log.warn("用户服务健康检查失败，响应: {}", response);
                return ApiResponse.error("用户服务健康检查失败");
            }
            
        } catch (Exception e) {
            log.error("用户服务健康检查异常，错误信息: {}", e.getMessage(), e);
            return ApiResponse.error("用户服务健康检查异常：" + e.getMessage());
        }
    }
}
