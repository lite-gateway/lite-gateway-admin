package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.TraceConfig;
import com.litegateway.admin.service.TraceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway/trace-config")
@Tag(name = "链路追踪配置管理", description = "分布式链路追踪配置")
public class TraceConfigController {

    @Autowired
    private TraceConfigService traceConfigService;

    @GetMapping("/page")
    @Operation(summary = "分页查询链路追踪配置")
    public Result<PageBody<TraceConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) Integer status) {

        Page<TraceConfig> page = traceConfigService.queryPage(configName, status, pageNum, pageSize);

        PageBody<TraceConfig> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有启用的链路追踪配置")
    public Result<List<TraceConfig>> listAll() {
        List<TraceConfig> list = traceConfigService.listAllEnabled();
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询链路追踪配置")
    public Result<TraceConfig> getById(@PathVariable Long id) {
        TraceConfig config = traceConfigService.getById(id);
        if (config != null) {
            List<String> routeIds = traceConfigService.getBoundRouteIds(config.getConfigId());
            config.setRouteIds(routeIds);
        }
        return Result.ok(config);
    }

    @PostMapping
    @Operation(summary = "新增链路追踪配置")
    public Result<Void> add(@RequestBody TraceConfig config) {
        traceConfigService.save(config);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改链路追踪配置")
    public Result<Void> update(@PathVariable Long id, @RequestBody TraceConfig config) {
        config.setId(id);
        traceConfigService.updateById(config);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除链路追踪配置")
    public Result<Void> delete(@PathVariable Long id) {
        traceConfigService.removeById(id);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "修改链路追踪配置状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        traceConfigService.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/bound-routes")
    @Operation(summary = "获取链路追踪配置已绑定的路由ID列表")
    public Result<List<String>> getBoundRoutes(@RequestParam String configId) {
        List<String> routeIds = traceConfigService.getBoundRouteIds(configId);
        return Result.ok(routeIds);
    }

    @PostMapping("/{configId}/bind-routes")
    @Operation(summary = "绑定路由到链路追踪配置")
    public Result<Void> bindRoutes(@PathVariable String configId, @RequestBody BindRoutesRequest request) {
        traceConfigService.bindRoutes(configId, request.getRouteIds());
        return Result.ok();
    }

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
