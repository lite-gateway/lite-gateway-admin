package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.RewriteRule;
import com.litegateway.admin.service.RewriteRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gateway/rewrite-rule")
public class RewriteRuleController {

    @Autowired
    private RewriteRuleService rewriteRuleService;

    @GetMapping("/list")
    public Result<IPage<RewriteRule>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String matchType,
            @RequestParam(required = false) String rewriteType,
            @RequestParam(required = false) Integer enabled) {
        Page<RewriteRule> pageParam = new Page<>(page, size);
        IPage<RewriteRule> result = rewriteRuleService.queryPage(pageParam, ruleName, matchType, rewriteType, enabled);
        return Result.success(result);
    }

    @GetMapping("/{ruleId}")
    public Result<RewriteRule> getById(@PathVariable String ruleId) {
        RewriteRule rule = rewriteRuleService.getByRuleId(ruleId);
        if (rule == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(rule);
    }

    @PostMapping
    public Result<RewriteRule> create(@RequestBody RewriteRule rule) {
        RewriteRule saved = rewriteRuleService.saveRule(rule);
        return Result.success(saved);
    }

    @PutMapping("/{ruleId}")
    public Result<RewriteRule> update(@PathVariable String ruleId, @RequestBody RewriteRule rule) {
        rule.setRuleId(ruleId);
        boolean success = rewriteRuleService.updateRule(rule);
        if (success) {
            return Result.success(rule);
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @DeleteMapping("/{ruleId}")
    public Result<Void> delete(@PathVariable String ruleId) {
        boolean success = rewriteRuleService.deleteRule(ruleId);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @PutMapping("/{ruleId}/status")
    public Result<Void> updateStatus(@PathVariable String ruleId, @RequestParam Integer enabled) {
        boolean success = rewriteRuleService.updateStatus(ruleId, enabled);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/enabled/all")
    public Result<List<RewriteRule>> getAllEnabled() {
        List<RewriteRule> list = rewriteRuleService.getAllEnabled();
        return Result.success(list);
    }

    @GetMapping("/match-types")
    public Result<List<String>> getMatchTypes() {
        return Result.success(Arrays.asList("EXACT", "PREFIX", "REGEX", "CONTAINS"));
    }

    @GetMapping("/rewrite-types")
    public Result<List<String>> getRewriteTypes() {
        return Result.success(Arrays.asList("PATH", "HOST", "METHOD", "HEADER", "QUERY", "BODY", "COMBINED"));
    }
}
