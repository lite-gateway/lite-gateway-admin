package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.CanaryRule;
import com.litegateway.admin.service.CanaryRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 灰度规则管理控制器
 */
@RestController
@RequestMapping("/gateway/canary")
@RequiredArgsConstructor
public class CanaryRuleController {

    @Autowired
    private CanaryRuleService canaryRuleService;

    /**
     * 分页查询灰度规则
     */
    @GetMapping("/page")
    public Result<PageBody<CanaryRule>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String routeId,
            @RequestParam(required = false) Boolean enabled) {

        Page<CanaryRule> page = canaryRuleService.queryPage(ruleName, routeId, enabled, pageNum, pageSize);

        PageBody<CanaryRule> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的灰度规则
     */
    @GetMapping("/list")
    public Result<List<CanaryRule>> listAll() {
        List<CanaryRule> list = canaryRuleService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据ID查询灰度规则
     */
    @GetMapping("/{id}")
    public Result<CanaryRule> getById(@PathVariable Long id) {
        CanaryRule rule = canaryRuleService.getById(id);
        return Result.ok(rule);
    }

    /**
     * 根据规则ID查询
     */
    @GetMapping("/rule/{ruleId}")
    public Result<CanaryRule> getByRuleId(@PathVariable String ruleId) {
        CanaryRule rule = canaryRuleService.getByRuleId(ruleId);
        return Result.ok(rule);
    }

    /**
     * 根据路由ID查询灰度规则
     */
    @GetMapping("/route/{routeId}")
    public Result<List<CanaryRule>> getByRouteId(@PathVariable String routeId) {
        List<CanaryRule> rules = canaryRuleService.listByRouteId(routeId);
        return Result.ok(rules);
    }

    /**
     * 新增灰度规则
     */
    @PostMapping
    public Result<Void> add(@RequestBody CanaryRule rule) {
        canaryRuleService.save(rule);
        return Result.ok();
    }

    /**
     * 修改灰度规则
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CanaryRule rule) {
        rule.setId(id);
        canaryRuleService.updateById(rule);
        return Result.ok();
    }

    /**
     * 删除灰度规则
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        canaryRuleService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改灰度规则状态
     */
    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        canaryRuleService.updateStatus(id, enabled);
        return Result.ok();
    }

    /**
     * 发布灰度规则变更事件
     */
    @PostMapping("/{ruleId}/publish")
    public Result<Void> publishChange(@PathVariable String ruleId) {
        canaryRuleService.publishRuleChangeEvent(ruleId);
        return Result.ok();
    }
}
