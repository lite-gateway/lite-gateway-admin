package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.TimeoutConfig;
import com.litegateway.admin.service.TimeoutConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 超时配置管理控制器
 */
@RestController
@RequestMapping("/gateway/timeout-config")
@Tag(name = "超时配置管理", description = "支持路由级别和全局级别的超时控制配置")
public class TimeoutConfigController {

    @Autowired
    private TimeoutConfigService timeoutConfigService;

    /**
     * 分页查询超时配置
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询超时配置")
    public Result<PageBody<TimeoutConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String configType,
            @RequestParam(required = false) Integer status) {

        Page<TimeoutConfig> page = timeoutConfigService.queryPage(configName, configType, status, pageNum, pageSize);

        PageBody<TimeoutConfig> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的超时配置（用于下拉选择）
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有启用的超时配置")
    public Result<List<TimeoutConfig>> listAll() {
        List<TimeoutConfig> list = timeoutConfigService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据配置类型查询
     */
    @GetMapping("/list/{configType}")
    @Operation(summary = "根据配置类型查询")
    public Result<List<TimeoutConfig>> listByConfigType(@PathVariable String configType) {
        List<TimeoutConfig> list = timeoutConfigService.listByConfigType(configType);
        return Result.ok(list);
    }

    /**
     * 获取全局配置
     */
    @GetMapping("/global")
    @Operation(summary = "获取全局超时配置")
    public Result<TimeoutConfig> getGlobalConfig() {
        TimeoutConfig config = timeoutConfigService.getGlobalConfig();
        return Result.ok(config);
    }

    /**
     * 根据ID查询超时配置
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询超时配置")
    public Result<TimeoutConfig> getById(@PathVariable Long id) {
        TimeoutConfig config = timeoutConfigService.getById(id);
        if (config != null && "route".equals(config.getConfigType())) {
            // 加载关联的路由ID
            List<String> routeIds = timeoutConfigService.getBoundRouteIds(config.getConfigId());
            config.setRouteIds(routeIds);
        }
        return Result.ok(config);
    }

    /**
     * 根据配置ID获取详情
     */
    @GetMapping("/config/{configId}")
    @Operation(summary = "根据配置ID查询")
    public Result<TimeoutConfig> getByConfigId(@PathVariable String configId) {
        TimeoutConfig config = timeoutConfigService.getByConfigId(configId);
        return Result.ok(config);
    }

    /**
     * 新增超时配置
     */
    @PostMapping
    @Operation(summary = "新增超时配置")
    public Result<Void> add(@RequestBody TimeoutConfig config) {
        timeoutConfigService.save(config);
        return Result.ok();
    }

    /**
     * 修改超时配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改超时配置")
    public Result<Void> update(@PathVariable Long id, @RequestBody TimeoutConfig config) {
        config.setId(id);
        timeoutConfigService.updateById(config);
        return Result.ok();
    }

    /**
     * 删除超时配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除超时配置")
    public Result<Void> delete(@PathVariable Long id) {
        timeoutConfigService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改超时配置状态
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "修改超时配置状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        timeoutConfigService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 根据路由ID查询关联的超时配置
     */
    @GetMapping("/route")
    @Operation(summary = "根据路由ID查询关联的超时配置")
    public Result<List<TimeoutConfig>> getTimeoutConfigsByRouteId(@RequestParam String routeId) {
        List<TimeoutConfig> configs = timeoutConfigService.listByRouteId(routeId);
        return Result.ok(configs);
    }

    /**
     * 获取超时配置已绑定的路由ID列表
     */
    @GetMapping("/bound-routes")
    @Operation(summary = "获取超时配置已绑定的路由ID列表")
    public Result<List<String>> getBoundRoutes(@RequestParam String configId) {
        List<String> routeIds = timeoutConfigService.getBoundRouteIds(configId);
        return Result.ok(routeIds);
    }

    /**
     * 绑定路由到超时配置
     */
    @PostMapping("/{configId}/bind-routes")
    @Operation(summary = "绑定路由到超时配置")
    public Result<Void> bindRoutes(@PathVariable String configId, @RequestBody BindRoutesRequest request) {
        timeoutConfigService.bindRoutes(configId, request.getRouteIds());
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
