package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.DesensitizationRule;
import com.litegateway.admin.service.DesensitizationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gateway/desensitization-rule")
public class DesensitizationRuleController {

    @Autowired
    private DesensitizationRuleService desensitizationRuleService;

    @GetMapping("/list")
    public Result<IPage<DesensitizationRule>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String dataType,
            @RequestParam(required = false) String desensitizationType,
            @RequestParam(required = false) Integer enabled) {
        Page<DesensitizationRule> pageParam = new Page<>(page, size);
        IPage<DesensitizationRule> result = desensitizationRuleService.queryPage(pageParam, ruleName, dataType, desensitizationType, enabled);
        return Result.success(result);
    }

    @GetMapping("/{ruleId}")
    public Result<DesensitizationRule> getById(@PathVariable String ruleId) {
        DesensitizationRule rule = desensitizationRuleService.getByRuleId(ruleId);
        if (rule == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(rule);
    }

    @PostMapping
    public Result<DesensitizationRule> create(@RequestBody DesensitizationRule rule) {
        DesensitizationRule saved = desensitizationRuleService.saveRule(rule);
        return Result.success(saved);
    }

    @PutMapping("/{ruleId}")
    public Result<DesensitizationRule> update(@PathVariable String ruleId, @RequestBody DesensitizationRule rule) {
        rule.setRuleId(ruleId);
        boolean success = desensitizationRuleService.updateRule(rule);
        if (success) {
            return Result.success(rule);
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @DeleteMapping("/{ruleId}")
    public Result<Void> delete(@PathVariable String ruleId) {
        boolean success = desensitizationRuleService.deleteRule(ruleId);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @PutMapping("/{ruleId}/status")
    public Result<Void> updateStatus(@PathVariable String ruleId, @RequestParam Integer enabled) {
        boolean success = desensitizationRuleService.updateStatus(ruleId, enabled);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/enabled/all")
    public Result<List<DesensitizationRule>> getAllEnabled() {
        List<DesensitizationRule> list = desensitizationRuleService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/data-types")
    public Result<List<String>> getDataTypes() {
        return Result.success(Arrays.asList("PHONE", "EMAIL", "ID_CARD", "BANK_CARD", "ADDRESS", "NAME", "PASSWORD", "CUSTOM"));
    }

    @GetMapping("/desensitization-types")
    public Result<List<String>> getDesensitizationTypes() {
        return Result.success(Arrays.asList("MASK", "TRUNCATE", "REPLACE", "HASH", "CUSTOM"));
    }
}
