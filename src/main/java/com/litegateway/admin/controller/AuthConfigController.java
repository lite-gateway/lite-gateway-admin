package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.AuthConfig;
import com.litegateway.admin.service.AuthConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 鉴权配置管理控制器
 */
@RestController
@RequestMapping("/gateway/auth-config")
@Tag(name = "鉴权配置管理", description = "支持 JWT、OAuth2、API Key、AK/SK 等多种鉴权方式的配置管理")
public class AuthConfigController {

    @Autowired
    private AuthConfigService authConfigService;

    /**
     * 分页查询鉴权配置
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询鉴权配置")
    public Result<PageBody<AuthConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String authType,
            @RequestParam(required = false) Integer status) {

        Page<AuthConfig> page = authConfigService.queryPage(configName, authType, status, pageNum, pageSize);

        PageBody<AuthConfig> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的鉴权配置（用于下拉选择）
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有启用的鉴权配置")
    public Result<List<AuthConfig>> listAll() {
        List<AuthConfig> list = authConfigService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据鉴权类型查询
     */
    @GetMapping("/list/{authType}")
    @Operation(summary = "根据鉴权类型查询")
    public Result<List<AuthConfig>> listByAuthType(@PathVariable String authType) {
        List<AuthConfig> list = authConfigService.listByAuthType(authType);
        return Result.ok(list);
    }

    /**
     * 根据ID查询鉴权配置
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询鉴权配置")
    public Result<AuthConfig> getById(@PathVariable Long id) {
        AuthConfig config = authConfigService.getById(id);
        if (config != null) {
            // 加载关联的路由ID
            List<String> routeIds = authConfigService.getBoundRouteIds(config.getConfigId());
            config.setRouteIds(routeIds);
        }
        return Result.ok(config);
    }

    /**
     * 根据配置ID查询
     */
    @GetMapping("/config/{configId}")
    @Operation(summary = "根据配置ID查询")
    public Result<AuthConfig> getByConfigId(@PathVariable String configId) {
        AuthConfig config = authConfigService.getByConfigId(configId);
        return Result.ok(config);
    }

    /**
     * 新增鉴权配置
     */
    @PostMapping
    @Operation(summary = "新增鉴权配置")
    public Result<Void> add(@RequestBody AuthConfig config) {
        authConfigService.save(config);
        return Result.ok();
    }

    /**
     * 修改鉴权配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改鉴权配置")
    public Result<Void> update(@PathVariable Long id, @RequestBody AuthConfig config) {
        config.setId(id);
        authConfigService.updateById(config);
        return Result.ok();
    }

    /**
     * 删除鉴权配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除鉴权配置")
    public Result<Void> delete(@PathVariable Long id) {
        authConfigService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改鉴权配置状态
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "修改鉴权配置状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        authConfigService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 根据路由ID查询关联的鉴权配置
     */
    @GetMapping("/route")
    @Operation(summary = "根据路由ID查询关联的鉴权配置")
    public Result<List<AuthConfig>> getAuthConfigsByRouteId(@RequestParam String routeId) {
        List<AuthConfig> configs = authConfigService.listByRouteId(routeId);
        return Result.ok(configs);
    }

    /**
     * 获取鉴权配置已绑定的路由ID列表
     */
    @GetMapping("/bound-routes")
    @Operation(summary = "获取鉴权配置已绑定的路由ID列表")
    public Result<List<String>> getBoundRoutes(@RequestParam String configId) {
        List<String> routeIds = authConfigService.getBoundRouteIds(configId);
        return Result.ok(routeIds);
    }

    /**
     * 绑定路由到鉴权配置
     */
    @PostMapping("/{configId}/bind-routes")
    @Operation(summary = "绑定路由到鉴权配置")
    public Result<Void> bindRoutes(@PathVariable String configId, @RequestBody BindRoutesRequest request) {
        authConfigService.bindRoutes(configId, request.getRouteIds());
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
