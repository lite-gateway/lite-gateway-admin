package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.entity.AiAgentEntity;
import com.litegateway.admin.mapper.AiAgentMapper;
import com.litegateway.core.common.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AI Agent管理Controller
 */
@Tag(name = "AI Agent管理", description = "AI Agent的增删改查")
@RestController
@RequestMapping("/api/ai/agents")
@RequiredArgsConstructor
public class AiAgentController {

    private final AiAgentMapper agentMapper;

    /**
     * 分页查询Agent列表
     */
    @Operation(summary = "分页查询Agent列表")
    @GetMapping
    public Result<Page<AiAgentEntity>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "类型") @RequestParam(required = false) String agentType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<AiAgentEntity> wrapper = new LambdaQueryWrapper<>()
                .orderByDesc(AiAgentEntity::getCreatedAt);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(AiAgentEntity::getAgentName, keyword)
                    .or()
                    .like(AiAgentEntity::getAgentId, keyword));
        }
        
        if (agentType != null && !agentType.isEmpty()) {
            wrapper.eq(AiAgentEntity::getAgentType, agentType);
        }
        
        if (status != null) {
            wrapper.eq(AiAgentEntity::getStatus, status);
        }
        
        Page<AiAgentEntity> result = agentMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取Agent详情
     */
    @Operation(summary = "获取Agent详情")
    @GetMapping("/{id}")
    public Result<AiAgentEntity> getById(@PathVariable Long id) {
        AiAgentEntity entity = agentMapper.selectById(id);
        if (entity == null) {
            return Result.error("Agent不存在");
        }
        // 不返回API Key
        entity.setApiKeyHash(null);
        return Result.success(entity);
    }

    /**
     * 创建Agent
     */
    @Operation(summary = "创建Agent")
    @PostMapping
    public Result<String> create(@RequestBody @Validated AiAgentEntity entity) {
        // 生成Agent ID
        String agentId = "agent-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        entity.setAgentId(agentId);
        
        // 生成API Key
        String apiKey = "sk-" + UUID.randomUUID().toString().replace("-", "");
        entity.setApiKeyHash(hashApiKey(apiKey));
        
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        agentMapper.insert(entity);
        
        // 返回Agent ID和API Key(仅创建时返回一次)
        return Result.success(agentId + ":" + apiKey);
    }

    /**
     * 更新Agent
     */
    @Operation(summary = "更新Agent")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated AiAgentEntity entity) {
        AiAgentEntity existing = agentMapper.selectById(id);
        if (existing == null) {
            return Result.error("Agent不存在");
        }
        
        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());
        // 不更新API Key
        entity.setApiKeyHash(null);
        entity.setAgentId(null);
        
        agentMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除Agent
     */
    @Operation(summary = "删除Agent")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        agentMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用Agent
     */
    @Operation(summary = "启用/禁用Agent")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AiAgentEntity entity = new AiAgentEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        
        agentMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 重置API Key
     */
    @Operation(summary = "重置API Key")
    @PutMapping("/{id}/reset-api-key")
    public Result<String> resetApiKey(@PathVariable Long id) {
        AiAgentEntity existing = agentMapper.selectById(id);
        if (existing == null) {
            return Result.error("Agent不存在");
        }
        
        String newApiKey = "sk-" + UUID.randomUUID().toString().replace("-", "");
        
        AiAgentEntity entity = new AiAgentEntity();
        entity.setId(id);
        entity.setApiKeyHash(hashApiKey(newApiKey));
        entity.setUpdatedAt(LocalDateTime.now());
        
        agentMapper.updateById(entity);
        
        return Result.success(newApiKey);
    }

    /**
     * 获取Agent统计
     */
    @Operation(summary = "获取Agent统计")
    @GetMapping("/{id}/stats")
    public Result<Object> getStats(@PathVariable Long id) {
        // TODO: 实现统计查询
        return Result.success(null);
    }

    /**
     * 简单的API Key哈希
     */
    private String hashApiKey(String apiKey) {
        // 实际应该使用更安全的哈希算法
        return org.springframework.util.DigestUtils.md5DigestAsHex(apiKey.getBytes());
    }
}
