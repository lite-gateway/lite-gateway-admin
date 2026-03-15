package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.entity.AiModelEntity;
import com.litegateway.admin.mapper.AiModelMapper;
import com.litegateway.core.common.web.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI模型管理Controller
 */
@Tag(name = "AI模型管理", description = "AI模型的增删改查")
@RestController
@RequestMapping("/api/ai/models")
@RequiredArgsConstructor
public class AiModelController {

    private final AiModelMapper modelMapper;

    /**
     * 分页查询模型列表
     */
    @Operation(summary = "分页查询模型列表")
    @GetMapping
    public Result<Page<AiModelEntity>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "提供商ID") @RequestParam(required = false) Long providerId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<AiModelEntity> wrapper = new LambdaQueryWrapper<>()
                .orderByDesc(AiModelEntity::getCreatedAt);
        
        if (providerId != null) {
            wrapper.eq(AiModelEntity::getProviderId, providerId);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(AiModelEntity::getModelName, keyword)
                    .or()
                    .like(AiModelEntity::getModelKey, keyword));
        }
        
        if (status != null) {
            wrapper.eq(AiModelEntity::getStatus, status);
        }
        
        Page<AiModelEntity> result = modelMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的模型
     */
    @Operation(summary = "获取所有启用的模型")
    @GetMapping("/enabled")
    public Result<List<AiModelEntity>> listEnabled(
            @Parameter(description = "提供商ID") @RequestParam(required = false) Long providerId) {
        
        LambdaQueryWrapper<AiModelEntity> wrapper = new LambdaQueryWrapper<>()
                .eq(AiModelEntity::getStatus, 1)
                .orderByAsc(AiModelEntity::getDisplayOrder);
        
        if (providerId != null) {
            wrapper.eq(AiModelEntity::getProviderId, providerId);
        }
        
        List<AiModelEntity> list = modelMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 获取模型详情
     */
    @Operation(summary = "获取模型详情")
    @GetMapping("/{id}")
    public Result<AiModelEntity> getById(@PathVariable Long id) {
        AiModelEntity entity = modelMapper.selectById(id);
        if (entity == null) {
            return Result.error("模型不存在");
        }
        return Result.success(entity);
    }

    /**
     * 创建模型
     */
    @Operation(summary = "创建模型")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated AiModelEntity entity) {
        // 检查同一提供商下模型Key是否已存在
        LambdaQueryWrapper<AiModelEntity> wrapper = new LambdaQueryWrapper<>()
                .eq(AiModelEntity::getProviderId, entity.getProviderId())
                .eq(AiModelEntity::getModelKey, entity.getModelKey());
        if (modelMapper.selectCount(wrapper) > 0) {
            return Result.error("该提供商下已存在相同模型标识");
        }
        
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        modelMapper.insert(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新模型
     */
    @Operation(summary = "更新模型")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated AiModelEntity entity) {
        AiModelEntity existing = modelMapper.selectById(id);
        if (existing == null) {
            return Result.error("模型不存在");
        }
        
        // 检查模型Key是否冲突
        if (!existing.getModelKey().equals(entity.getModelKey())) {
            LambdaQueryWrapper<AiModelEntity> wrapper = new LambdaQueryWrapper<>()
                    .eq(AiModelEntity::getProviderId, entity.getProviderId())
                    .eq(AiModelEntity::getModelKey, entity.getModelKey());
            if (modelMapper.selectCount(wrapper) > 0) {
                return Result.error("该提供商下已存在相同模型标识");
            }
        }
        
        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());
        
        modelMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除模型
     */
    @Operation(summary = "删除模型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        modelMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用模型
     */
    @Operation(summary = "启用/禁用模型")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AiModelEntity entity = new AiModelEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        
        modelMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 设置默认模型
     */
    @Operation(summary = "设置默认模型")
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        AiModelEntity model = modelMapper.selectById(id);
        if (model == null) {
            return Result.error("模型不存在");
        }
        
        // 清除该提供商下的其他默认模型
        AiModelEntity clearDefault = new AiModelEntity();
        clearDefault.setIsDefault(0);
        LambdaQueryWrapper<AiModelEntity> wrapper = new LambdaQueryWrapper<>()
                .eq(AiModelEntity::getProviderId, model.getProviderId())
                .eq(AiModelEntity::getIsDefault, 1);
        modelMapper.update(clearDefault, wrapper);
        
        // 设置当前模型为默认
        AiModelEntity setDefault = new AiModelEntity();
        setDefault.setId(id);
        setDefault.setIsDefault(1);
        setDefault.setUpdatedAt(LocalDateTime.now());
        modelMapper.updateById(setDefault);
        
        return Result.success();
    }

    /**
     * 批量导入模型
     */
    @Operation(summary = "批量导入模型")
    @PostMapping("/batch-import")
    public Result<Void> batchImport(@RequestBody List<AiModelEntity> models) {
        for (AiModelEntity entity : models) {
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            modelMapper.insert(entity);
        }
        return Result.success();
    }
}
