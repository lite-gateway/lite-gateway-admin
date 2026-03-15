package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.RateLimitRule;
import com.litegateway.admin.service.RateLimitRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 限流规则管理控制器
 */
@RestController
@RequestMapping("/gateway/rate-limit")
public class RateLimitRuleController {

    @Autowired
    private RateLimitRuleService rateLimitRuleService;

    /**
     * 分页查询限流规则
     */
    @GetMapping("/page")
    public Result<PageBody<RateLimitRule>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) Integer limitType,
            @RequestParam(required = false) Integer status) {
        
        Page<RateLimitRule> page = rateLimitRuleService.queryPage(ruleName, limitType, status, pageNum, pageSize);
        
        PageBody<RateLimitRule> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());
        
        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的限流规则（用于下拉选择）
     */
    @GetMapping("/list")
    public Result<List<RateLimitRule>> listAll() {
        List<RateLimitRule> list = rateLimitRuleService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据ID查询限流规则
     */
    @GetMapping("/{id}")
    public Result<RateLimitRule> getById(@PathVariable Long id) {
        RateLimitRule rule = rateLimitRuleService.getById(id);
        if (rule != null) {
            // 加载关联的路由ID
            List<String> routeIds = rateLimitRuleService.getBoundRouteIds(rule.getRuleId());
            rule.setRouteIds(routeIds);
        }
        return Result.ok(rule);
    }

    /**
     * 新增限流规则
     */
    @PostMapping
    public Result<Void> add(@RequestBody RateLimitRule rule) {
        rateLimitRuleService.save(rule);
        return Result.ok();
    }

    /**
     * 修改限流规则
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody RateLimitRule rule) {
        rule.setId(id);
        rateLimitRuleService.updateById(rule);
        return Result.ok();
    }

    /**
     * 删除限流规则
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        rateLimitRuleService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改限流规则状态
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        rateLimitRuleService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 根据路由ID查询关联的限流规则
     */
    @GetMapping("/route")
    public Result<List<RateLimitRule>> getRateLimitsByRouteId(@RequestParam String routeId) {
        List<RateLimitRule> rules = rateLimitRuleService.listByRouteId(routeId);
        return Result.ok(rules);
    }

    /**
     * 获取限流规则已绑定的路由ID列表
     */
    @GetMapping("/bound-routes")
    public Result<List<String>> getBoundRoutes(@RequestParam String ruleId) {
        List<String> routeIds = rateLimitRuleService.getBoundRouteIds(ruleId);
        return Result.ok(routeIds);
    }

    /**
     * 绑定路由到限流规则
     */
    @PostMapping("/{ruleId}/bind-routes")
    public Result<Void> bindRoutes(@PathVariable String ruleId, @RequestBody BindRoutesRequest request) {
        rateLimitRuleService.bindRoutes(ruleId, request.getRouteIds());
        return Result.ok();
    }

    /**
     * 绑定路由请求体
     */
    public static class BindRoutesRequest {
        private List<String> routeIds;

        public List<String> getRouteIds() {
            return routeIds;
        }

        public void setRouteIds(List<String> routeIds) {
            this.routeIds = routeIds;
        }
    }
}
