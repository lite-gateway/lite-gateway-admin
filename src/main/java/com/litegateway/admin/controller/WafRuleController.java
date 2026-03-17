package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.WafRule;
import com.litegateway.admin.repository.mapper.WafRuleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WAF规则管理Controller
 */
@Tag(name = "WAF规则管理", description = "WAF防火墙规则的增删改查")
@RestController
@RequestMapping("/gateway/waf")
@RequiredArgsConstructor
public class WafRuleController {

    private final WafRuleMapper wafRuleMapper;

    /**
     * 分页查询WAF规则列表
     */
    @Operation(summary = "分页查询WAF规则列表")
    @GetMapping
    public Result<Page<WafRule>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "规则类型") @RequestParam(required = false) String ruleType,
            @Parameter(description = "风险等级") @RequestParam(required = false) String riskLevel,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<WafRule> wrapper = new LambdaQueryWrapper<WafRule>()
                .orderByDesc(WafRule::getPriority)
                .orderByDesc(WafRule::getCreatedAt);

        if (ruleType != null && !ruleType.isEmpty()) {
            wrapper.eq(WafRule::getRuleType, ruleType);
        }

        if (riskLevel != null && !riskLevel.isEmpty()) {
            wrapper.eq(WafRule::getRiskLevel, riskLevel);
        }

        if (status != null) {
            wrapper.eq(WafRule::getStatus, status);
        }

        Page<WafRule> result = wafRuleMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(result);
    }

    /**
     * 获取所有启用的WAF规则
     */
    @Operation(summary = "获取所有启用的WAF规则")
    @GetMapping("/enabled")
    public Result<List<WafRule>> listEnabled() {
        LambdaQueryWrapper<WafRule> wrapper = new LambdaQueryWrapper<WafRule>()
                .eq(WafRule::getStatus, 1)
                .orderByDesc(WafRule::getPriority);

        List<WafRule> list = wafRuleMapper.selectList(wrapper);
        return Result.success(list);
    }

    /**
     * 获取WAF规则详情
     */
    @Operation(summary = "获取WAF规则详情")
    @GetMapping("/{id}")
    public Result<WafRule> getById(@PathVariable Long id) {
        WafRule entity = wafRuleMapper.selectById(id);
        if (entity == null) {
            return Result.error("规则不存在");
        }
        return Result.success(entity);
    }

    /**
     * 创建WAF规则
     */
    @Operation(summary = "创建WAF规则")
    @PostMapping
    public Result<Long> create(@RequestBody @Validated WafRule entity) {
        entity.setHitCount(0L);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        wafRuleMapper.insert(entity);
        return Result.success(entity.getId());
    }

    /**
     * 更新WAF规则
     */
    @Operation(summary = "更新WAF规则")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated WafRule entity) {
        WafRule existing = wafRuleMapper.selectById(id);
        if (existing == null) {
            return Result.error("规则不存在");
        }

        entity.setId(id);
        entity.setUpdatedAt(LocalDateTime.now());

        wafRuleMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 删除WAF规则
     */
    @Operation(summary = "删除WAF规则")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wafRuleMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 启用/禁用WAF规则
     */
    @Operation(summary = "启用/禁用WAF规则")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        WafRule entity = new WafRule();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUpdatedAt(LocalDateTime.now());

        wafRuleMapper.updateById(entity);
        return Result.success();
    }

    /**
     * 更新WAF规则优先级
     */
    @Operation(summary = "更新WAF规则优先级")
    @PutMapping("/{id}/priority")
    public Result<Void> updatePriority(@PathVariable Long id, @RequestParam Integer priority) {
        WafRule entity = new WafRule();
        entity.setId(id);
        entity.setPriority(priority);
        entity.setUpdatedAt(LocalDateTime.now());

        wafRuleMapper.updateById(entity);
        return Result.success();
    }
}
