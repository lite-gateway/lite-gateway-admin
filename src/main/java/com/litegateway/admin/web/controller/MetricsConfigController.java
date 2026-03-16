package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.AlertRule;
import com.litegateway.admin.repository.entity.MetricsConfig;
import com.litegateway.admin.service.MetricsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway/metrics-config")
public class MetricsConfigController {

    @Autowired
    private MetricsConfigService metricsConfigService;

    @GetMapping("/current")
    public Result<MetricsConfig> getCurrentConfig() {
        MetricsConfig config = metricsConfigService.getActiveConfig();
        if (config == null) {
            config = new MetricsConfig();
            config.setStatus(0);
        }
        return Result.success(config);
    }

    @PostMapping("/save")
    public Result<MetricsConfig> saveConfig(@RequestBody MetricsConfig config) {
        MetricsConfig saved = metricsConfigService.saveOrUpdateConfig(config);
        return Result.success(saved);
    }

    @GetMapping("/alert-rules")
    public Result<Page<AlertRule>> listAlertRules(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) Integer status) {
        Page<AlertRule> result = metricsConfigService.queryAlertRulePage(ruleName, status, page, size);
        return Result.success(result);
    }

    @GetMapping("/alert-rules/{ruleId}")
    public Result<AlertRule> getAlertRule(@PathVariable Long ruleId) {
        AlertRule rule = metricsConfigService.getAlertRuleById(ruleId);
        if (rule == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(rule);
    }

    @PostMapping("/alert-rules")
    public Result<AlertRule> createAlertRule(@RequestBody AlertRule rule) {
        metricsConfigService.saveAlertRule(rule);
        return Result.success(rule);
    }

    @PutMapping("/alert-rules/{ruleId}")
    public Result<AlertRule> updateAlertRule(@PathVariable Long ruleId, @RequestBody AlertRule rule) {
        rule.setId(ruleId);
        metricsConfigService.updateAlertRule(rule);
        return Result.success(rule);
    }

    @DeleteMapping("/alert-rules/{ruleId}")
    public Result<Void> deleteAlertRule(@PathVariable Long ruleId) {
        metricsConfigService.deleteAlertRule(ruleId);
        return Result.success();
    }

    @PutMapping("/alert-rules/{ruleId}/status")
    public Result<Void> updateAlertRuleStatus(@PathVariable Long ruleId, @RequestParam Integer status) {
        metricsConfigService.updateAlertRuleStatus(ruleId, status);
        return Result.success();
    }

    @GetMapping("/alert-rules/enabled/all")
    public Result<List<AlertRule>> getAllEnabledAlertRules() {
        List<AlertRule> rules = metricsConfigService.listAllEnabledAlertRules();
        return Result.success(rules);
    }
}
