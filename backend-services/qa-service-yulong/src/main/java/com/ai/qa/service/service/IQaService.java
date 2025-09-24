package com.ai.qa.service.service;

import com.ai.qa.service.dto.QaRequest;
import com.ai.qa.service.dto.QaResponse;

import java.util.List;

/**
 * 问答服务接口
 * 
 * 定义问答相关的业务操作规范，遵循面向接口编程原则
 * 提供AI问答、历史记录管理、搜索等核心业务方法的抽象定义
 * 
 * 设计原则：
 * 1. 面向接口编程：依赖抽象而不是具体实现
 * 2. 单一职责：专注于问答相关的业务逻辑
 * 3. 开闭原则：对扩展开放，对修改关闭
 * 4. 依赖倒置：高层模块不依赖低层模块，都依赖抽象
 * 
 * @author Leon
 * @version 1.0
 * @since 2025-09-06
 */
public interface IQaService {
    
    /**
     * 处理用户问答请求
     * 
     * 核心业务流程：
     * 1. 构建包含上下文的完整问题
     * 2. 调用AI服务获取回答
     * 3. 保存问答历史到数据库
     * 4. 返回格式化的响应
     * 
     * @param request 问答请求，包含用户ID、问题内容等
     * @return QaResponse 问答响应，包含问题、答案、时间等信息
     * @throws RuntimeException 当AI服务调用失败时抛出异常
     */
    QaResponse askQuestion(QaRequest request);
    
    /**
     * 获取用户问答历史
     * 
     * 返回指定用户的所有问答记录，按时间倒序排列
     * 
     * @param userId 用户ID
     * @return List<QaResponse> 问答历史列表
     */
    List<QaResponse> getUserHistory(Long userId);
    
    /**
     * 分页获取用户问答历史
     * 
     * 支持分页查询，避免一次性返回过多数据
     * 适用于前端分页显示场景
     * 
     * @param userId 用户ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return List<QaResponse> 分页的问答历史列表
     */
    List<QaResponse> getUserHistoryPaged(Long userId, int page, int size);
    
    /**
     * 根据关键词搜索用户问答历史
     * 
     * 在用户的问题和答案中搜索包含指定关键词的记录
     * 支持模糊匹配，不区分大小写
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @return List<QaResponse> 匹配的问答历史列表
     */
    List<QaResponse> searchUserHistory(Long userId, String keyword);
    
    /**
     * 获取用户问答统计信息
     * 
     * 统计指定用户的问答记录总数
     * 用于用户数据统计和分析
     * 
     * @param userId 用户ID
     * @return long 问答总数
     */
    long getUserQaCount(Long userId);
    
    /**
     * 根据ID获取问答记录
     * 
     * 获取指定ID的问答记录详情
     * 用于问答记录的详细查看
     * 
     * @param id 问答记录ID
     * @return QaResponse 问答记录（如果存在），否则返回null
     */
    QaResponse getQaById(Long id);
    
    /**
     * 删除问答记录
     * 
     * 根据ID删除指定的问答记录
     * 通常用于用户删除自己的问答历史
     * 
     * @param id 问答记录ID
     * @param userId 用户ID（用于权限验证）
     * @return boolean true-删除成功，false-删除失败或记录不存在
     */
    boolean deleteQaRecord(Long id, Long userId);
    
    /**
     * 批量删除用户问答记录
     * 
     * 删除指定用户的所有问答记录
     * 通常用于用户注销账户时的数据清理
     * 
     * @param userId 用户ID
     * @return int 删除的记录数量
     */
    int deleteUserAllQaRecords(Long userId);
}
