package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.HealthCheckConfig;
import com.litegateway.admin.service.HealthCheckConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway/health-check-config")
@Tag(name = "健康检查配置管理", description = "服务实例健康检查策略配置")
public class HealthCheckConfigController {

    @Autowired
    private HealthCheckConfigService healthCheckConfigService;

    @GetMapping("/page")
    @Operation(summary = "分页查询健康检查配置")
    public Result<PageBody<HealthCheckConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) Integer status) {

        Page<HealthCheckConfig> page = healthCheckConfigService.queryPage(configName, status, pageNum, pageSize);

        PageBody<HealthCheckConfig> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有启用的健康检查配置")
    public Result<List<HealthCheckConfig>> listAll() {
        List<HealthCheckConfig> list = healthCheckConfigService.listAllEnabled();
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询健康检查配置")
    public Result<HealthCheckConfig> getById(@PathVariable Long id) {
        HealthCheckConfig config = healthCheckConfigService.getById(id);
        if (config != null) {
            List<String> serviceIds = healthCheckConfigService.getBoundServiceIds(config.getConfigId());
            config.setServiceIds(serviceIds);
        }
        return Result.ok(config);
    }

    @PostMapping
    @Operation(summary = "新增健康检查配置")
    public Result<Void> add(@RequestBody HealthCheckConfig config) {
        healthCheckConfigService.save(config);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改健康检查配置")
    public Result<Void> update(@PathVariable Long id, @RequestBody HealthCheckConfig config) {
        config.setId(id);
        healthCheckConfigService.updateById(config);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除健康检查配置")
    public Result<Void> delete(@PathVariable Long id) {
        healthCheckConfigService.removeById(id);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "修改健康检查配置状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        healthCheckConfigService.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/bound-services")
    @Operation(summary = "获取健康检查配置已绑定的服务ID列表")
    public Result<List<String>> getBoundServices(@RequestParam String configId) {
        List<String> serviceIds = healthCheckConfigService.getBoundServiceIds(configId);
        return Result.ok(serviceIds);
    }

    @PostMapping("/{configId}/bind-services")
    @Operation(summary = "绑定服务到健康检查配置")
    public Result<Void> bindServices(@PathVariable String configId, @RequestBody BindServicesRequest request) {
        healthCheckConfigService.bindServices(configId, request.getServiceIds());
        return Result.ok();
    }

    public static class BindServicesRequest {
        private List<String> serviceIds;

        public List<String> getServiceIds() {
            return serviceIds;
        }

        public void setServiceIds(List<String> serviceIds) {
            this.serviceIds = serviceIds;
        }
    }
}
