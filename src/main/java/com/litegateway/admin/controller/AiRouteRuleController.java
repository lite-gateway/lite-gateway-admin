package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.entity.AiRouteRuleEntity;
import com.litegateway.admin.mapper.AiRouteRuleMapper;
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
 * AI路由规则管理Controller
 */
@Tag(name = "AI路由规则管理", description = "AI路由规则的增删改查")
@RestController
@RequestMapping("/api/ai/route-rules")
@RequiredArgsConstructor
public class AiRouteRuleController {

    private final AiRouteRuleMapper routeRuleMapper;

    /**
     * 分页查询路由规则列表
     */
    @Operation(summary = "分页查询路由规则列表")
    @GetMapping
    public Result<Page<AiRouteRuleEntity>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<AiRouteRuleEntity> wrapper = new LambdaQueryWrapper<>()
                .orderByAsc(AiRouteRuleEntity::getPriority);
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(AiRouteRuleEntity::getRuleName, keyword)
                    .or()
                    .like(AiRouteRuleEntity::getRuleCode, keyword));
        }
        
        if (status != null) {
            wrapper.eq(AiRouteRuleEntity::getStatus, status);
        }
        
        Page<AiRouteRuleEntity> result = routeRuleMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的路由规则
     */
    @Operation(summary = "获取所有启用的路由规则")
    @GetMapping("/enabled")
    public Result<List<AiRouteRuleEntity>> listEnabled() {
        LambdaQueryWrapper<AiRouteRuleEntity> wrapper = new LambdaQueryWrapper<>()
                .eq(AiRouteRuleEntity::getStatus, 1)
                .orderByAsc(AiRouteRuleEntity::getPriority);
        
        List<AiRouteRuleEntity> list = routeRuleMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 获取路由规则详情
     */
    @Operation(summary = "获取路由规则详情")
    @GetMapping("/{id}")
    public Result<AiRouteRuleEntity> getById(@PathVariable Long id) {
        AiRouteRuleEntity entity = routeRuleMapper.selectById(id);
        if (entity == null) {
            return Result.error("路由规则不存在");
        }
        return Result.success(entity);
    }

    /**
     * 创建路由规则
     */
    @Operation(summary = "创建路由规则")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated AiRouteRuleEntity entity) {
        // 检查编码是否已存在
        LambdaQueryWrapper<AiRouteRuleEntity> wrapper = new LambdaQueryWrapper<>()
                .eq(AiRouteRuleEntity::getRuleCode, entity.getRuleCode());
        if (routeRuleMapper.selectCount(wrapper) > 0) {
            return Result.error("路由规则编码已存在");
        }
        
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        routeRuleMapper.insert(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新路由规则
     */
    @Operation(summary = "更新路由规则")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated AiRouteRuleEntity entity) {
        AiRouteRuleEntity existing = routeRuleMapper.selectById(id);
        if (existing == null) {
            return Result.error("路由规则不存在");
        }
        
        // 检查编码是否冲突
        if (!existing.getRuleCode().equals(entity.getRuleCode())) {
            LambdaQueryWrapper<AiRouteRuleEntity> wrapper = new LambdaQueryWrapper<>()
                    .eq(AiRouteRuleEntity::getRuleCode, entity.getRuleCode());
            if (routeRuleMapper.selectCount(wrapper) > 0) {
                return Result.error("路由规则编码已存在");
            }
        }
        
        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());
        
        routeRuleMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除路由规则
     */
    @Operation(summary = "删除路由规则")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        routeRuleMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用路由规则
     */
    @Operation(summary = "启用/禁用路由规则")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AiRouteRuleEntity entity = new AiRouteRuleEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());
        
        routeRuleMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 更新路由规则优先级
     */
    @Operation(summary = "更新路由规则优先级")
    @PutMapping("/{id}/priority")
    public Result<Void> updatePriority(@PathVariable Long id, @RequestParam Integer priority) {
        AiRouteRuleEntity entity = new AiRouteRuleEntity();
        entity.setId(id);
        entity.setPriority(priority);
        entity.setUpdatedAt(LocalDateTime.now());
        
        routeRuleMapper.updateById(entity);
        return Result.success();
    }
}
