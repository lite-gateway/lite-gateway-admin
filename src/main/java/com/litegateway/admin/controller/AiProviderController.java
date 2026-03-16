package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.entity.AiProviderEntity;
import com.litegateway.admin.mapper.AiProviderMapper;
import com.litegateway.admin.common.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI提供商管理Controller
 */
@Tag(name = "AI提供商管理", description = "AI提供商的增删改查")
@RestController
@RequestMapping("/api/ai/providers")
@RequiredArgsConstructor
public class AiProviderController {

    private final AiProviderMapper providerMapper;

    /**
     * 分页查询提供商列表
     */
    @Operation(summary = "分页查询提供商列表")
    @GetMapping
    public Result<Page<AiProviderEntity>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        QueryWrapper<AiProviderEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("provider_name", keyword).or().like("provider_code", keyword));
        }

        if (status != null) {
            wrapper.eq("status", status);
        }

        Page<AiProviderEntity> result = providerMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的提供商
     */
    @Operation(summary = "获取所有启用的提供商")
    @GetMapping("/enabled")
    public Result<List<AiProviderEntity>> listEnabled() {
        QueryWrapper<AiProviderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        wrapper.orderByAsc("priority");

        List<AiProviderEntity> list = providerMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 获取提供商详情
     */
    @Operation(summary = "获取提供商详情")
    @GetMapping("/{id}")
    public Result<AiProviderEntity> getById(@PathVariable Long id) {
        AiProviderEntity entity = providerMapper.selectById(id);
        if (entity == null) {
            return Result.error("提供商不存在");
        }
        // 不返回API Key
        entity.setApiKeyEncrypted(null);
        return Result.success(entity);
    }

    /**
     * 创建提供商
     */
    @Operation(summary = "创建提供商")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated AiProviderEntity entity) {
        // 检查编码是否已存在
        QueryWrapper<AiProviderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("provider_code", entity.getProviderCode());
        if (providerMapper.selectCount(wrapper) > 0) {
            return Result.error("提供商编码已存在");
        }

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setHealthStatus("unknown");

        providerMapper.insert(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新提供商
     */
    @Operation(summary = "更新提供商")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated AiProviderEntity entity) {
        AiProviderEntity existing = providerMapper.selectById(id);
        if (existing == null) {
            return Result.error("提供商不存在");
        }

        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());

        // 如果不传API Key，保持原值
        if (entity.getApiKeyEncrypted() == null || entity.getApiKeyEncrypted().isEmpty()) {
            entity.setApiKeyEncrypted(existing.getApiKeyEncrypted());
        }

        providerMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除提供商
     */
    @Operation(summary = "删除提供商")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        providerMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用提供商
     */
    @Operation(summary = "启用/禁用提供商")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AiProviderEntity entity = new AiProviderEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());

        providerMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 测试提供商连接
     */
    @Operation(summary = "测试提供商连接")
    @PostMapping("/{id}/test")
    public Result<String> testConnection(@PathVariable Long id) {
        AiProviderEntity entity = providerMapper.selectById(id);
        if (entity == null) {
            return Result.error("提供商不存在");
        }

        // TODO: 实现连接测试逻辑
        return Result.success("连接成功");
    }
}
