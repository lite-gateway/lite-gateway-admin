package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.CircuitBreakerRule;
import com.litegateway.admin.service.CircuitBreakerRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 熔断规则管理控制器
 */
@RestController
@RequestMapping("/gateway/circuit-breaker")
@RequiredArgsConstructor
public class CircuitBreakerRuleController {

    @Autowired
    private CircuitBreakerRuleService circuitBreakerRuleService;

    /**
     * 分页查询熔断规则
     */
    @GetMapping("/page")
    public Result<PageBody<CircuitBreakerRule>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) Boolean enabled) {

        Page<CircuitBreakerRule> page = circuitBreakerRuleService.queryPage(ruleName, routeId, enabled, pageNum, pageSize);

        PageBody<CircuitBreakerRule> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的熔断规则
     */
    @GetMapping("/list")
    public Result<List<CircuitBreakerRule>> listAll() {
        List<CircuitBreakerRule> list = circuitBreakerRuleService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据ID查询熔断规则
     */
    @GetMapping("/{id}")
    public Result<CircuitBreakerRule> getById(@PathVariable Long id) {
        CircuitBreakerRule rule = circuitBreakerRuleService.getById(id);
        return Result.ok(rule);
    }

    /**
     * 根据规则ID查询
     */
    @GetMapping("/rule/{ruleId}")
    public Result<CircuitBreakerRule> getByRuleId(@PathVariable String ruleId) {
        CircuitBreakerRule rule = circuitBreakerRuleService.getByRuleId(ruleId);
        return Result.ok(rule);
    }

    /**
     * 获取全局熔断规则
     */
    @GetMapping("/global")
    public Result<CircuitBreakerRule> getGlobalRule() {
        CircuitBreakerRule rule = circuitBreakerRuleService.getGlobalRule();
        return Result.ok(rule);
    }

    /**
     * 根据路由ID查询熔断规则
     * 支持 /route/{routeId} 和 /route?routeId=xxx 两种形式
     */
    @GetMapping("/route/{routeId}")
    public Result<List<CircuitBreakerRule>> getByRouteIdPath(@PathVariable String routeId) {
        List<CircuitBreakerRule> rules = circuitBreakerRuleService.listByRouteId(routeId);
        return Result.ok(rules);
    }

    /**
     * 根据路由ID查询熔断规则（Query参数形式）
     */
    @GetMapping("/route")
    public Result<List<CircuitBreakerRule>> getByRouteIdQuery(@RequestParam String routeId) {
        List<CircuitBreakerRule> rules = circuitBreakerRuleService.listByRouteId(routeId);
        return Result.ok(rules);
    }

    /**
     * 新增熔断规则
     */
    @PostMapping
    public Result<Void> add(@RequestBody CircuitBreakerRule rule) {
        circuitBreakerRuleService.save(rule);
        return Result.ok();
    }

    /**
     * 修改熔断规则
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CircuitBreakerRule rule) {
        rule.setId(id);
        circuitBreakerRuleService.updateById(rule);
        return Result.ok();
    }

    /**
     * 删除熔断规则
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        circuitBreakerRuleService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改熔断规则状态
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        circuitBreakerRuleService.updateStatus(id, enabled);
        return Result.ok();
    }

    /**
     * 发布熔断规则变更事件
     */
    @PostMapping("/{ruleId}/publish")
    public Result<Void> publishChange(@PathVariable String ruleId) {
        circuitBreakerRuleService.publishRuleChangeEvent(ruleId);
        return Result.ok();
    }
}
