package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.repository.entity.PolicyTemplate;
import com.litegateway.admin.repository.mapper.PolicyTemplateMapper;
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
 * 策略模板管理Controller
 */
@Tag(name = "策略模板管理", description = "策略模板的增删改查")
@RestController
@RequestMapping("/gateway/policy-template")
@RequiredArgsConstructor
public class PolicyTemplateController {

    private final PolicyTemplateMapper policyTemplateMapper;

    /**
     * 分页查询策略模板列表
     */
    @Operation(summary = "分页查询策略模板列表")
    @GetMapping
    public Result<Page<PolicyTemplate>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "策略类型") @RequestParam(required = false) String policyType,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<PolicyTemplate> wrapper = new LambdaQueryWrapper<PolicyTemplate>()
                .orderByDesc(PolicyTemplate::getUsageCount);

        if (policyType != null && !policyType.isEmpty()) {
            wrapper.eq(PolicyTemplate::getPolicyType, policyType);
        }

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(PolicyTemplate::getTemplateName, keyword)
                    .or()
                    .like(PolicyTemplate::getTemplateCode, keyword));
        }

        if (status != null) {
            wrapper.eq(PolicyTemplate::getStatus, status);
        }

        Page<PolicyTemplate> result = policyTemplateMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的策略模板
     */
    @Operation(summary = "获取所有启用的策略模板")
    @GetMapping("/enabled")
    public Result<List<PolicyTemplate>> listEnabled(
            @Parameter(description = "策略类型") @RequestParam(required = false) String policyType) {

        LambdaQueryWrapper<PolicyTemplate> wrapper = new LambdaQueryWrapper<PolicyTemplate>()
                .eq(PolicyTemplate::getStatus, 1);

        if (policyType != null && !policyType.isEmpty()) {
            wrapper.eq(PolicyTemplate::getPolicyType, policyType);
        }

        List<PolicyTemplate> list = policyTemplateMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 获取策略模板详情
     */
    @Operation(summary = "获取策略模板详情")
    @GetMapping("/{id}")
    public Result<PolicyTemplate> getById(@PathVariable Long id) {
        PolicyTemplate entity = policyTemplateMapper.selectById(id);
        if (entity == null) {
            return Result.error("模板不存在");
        }
        return Result.success(entity);
    }

    /**
     * 创建策略模板
     */
    @Operation(summary = "创建策略模板")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated PolicyTemplate entity) {
        entity.setUsageCount(0L);
        entity.setIsSystem(0);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        policyTemplateMapper.insert(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新策略模板
     */
    @Operation(summary = "更新策略模板")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated PolicyTemplate entity) {
        PolicyTemplate existing = policyTemplateMapper.selectById(id);
        if (existing == null) {
            return Result.error("模板不存在");
        }

        // 系统预设模板不允许修改编码
        if (existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            entity.setTemplateCode(null);
        }

        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());

        policyTemplateMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除策略模板
     */
    @Operation(summary = "删除策略模板")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        PolicyTemplate existing = policyTemplateMapper.selectById(id);
        if (existing != null && existing.getIsSystem() != null && existing.getIsSystem() == 1) {
            return Result.error("系统预设模板不能删除");
        }

        policyTemplateMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用策略模板
     */
    @Operation(summary = "启用/禁用策略模板")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        PolicyTemplate entity = new PolicyTemplate();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());

        policyTemplateMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 使用模板（增加使用次数）
     */
    @Operation(summary = "使用模板")
    @PostMapping("/{id}/use")
    public Result<Void> useTemplate(@PathVariable Long id) {
        policyTemplateMapper.incrementUsageCount(id);
        return Result.success();
    }
}
