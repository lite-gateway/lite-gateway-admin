package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.LoadBalanceConfig;
import com.litegateway.admin.service.LoadBalanceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 负载均衡配置管理控制器
 */
@RestController
@RequestMapping("/gateway/load-balance-config")
@Tag(name = "负载均衡配置管理", description = "支持轮询、随机、加权、最少连接、一致性哈希等多种负载均衡策略")
public class LoadBalanceConfigController {

    @Autowired
    private LoadBalanceConfigService loadBalanceConfigService;

    /**
     * 分页查询负载均衡配置
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询负载均衡配置")
    public Result<PageBody<LoadBalanceConfig>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String configName,
            @RequestParam(required = false) String strategy,
            @RequestParam(required = false) Integer status) {

        Page<LoadBalanceConfig> page = loadBalanceConfigService.queryPage(configName, strategy, status, pageNum, pageSize);

        PageBody<LoadBalanceConfig> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有启用的负载均衡配置（用于下拉选择）
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有启用的负载均衡配置")
    public Result<List<LoadBalanceConfig>> listAll() {
        List<LoadBalanceConfig> list = loadBalanceConfigService.listAllEnabled();
        return Result.ok(list);
    }

    /**
     * 根据策略类型查询
     */
    @GetMapping("/list/{strategy}")
    @Operation(summary = "根据策略类型查询")
    public Result<List<LoadBalanceConfig>> listByStrategy(@PathVariable String strategy) {
        List<LoadBalanceConfig> list = loadBalanceConfigService.listByStrategy(strategy);
        return Result.ok(list);
    }

    /**
     * 根据ID查询负载均衡配置
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询负载均衡配置")
    public Result<LoadBalanceConfig> getById(@PathVariable Long id) {
        LoadBalanceConfig config = loadBalanceConfigService.getById(id);
        if (config != null) {
            // 加载关联的服务ID
            List<String> serviceIds = loadBalanceConfigService.getBoundServiceIds(config.getConfigId());
            config.setServiceIds(serviceIds);
        }
        return Result.ok(config);
    }

    /**
     * 根据配置ID获取详情
     */
    @GetMapping("/config/{configId}")
    @Operation(summary = "根据配置ID查询")
    public Result<LoadBalanceConfig> getByConfigId(@PathVariable String configId) {
        LoadBalanceConfig config = loadBalanceConfigService.getByConfigId(configId);
        return Result.ok(config);
    }

    /**
     * 新增负载均衡配置
     */
    @PostMapping
    @Operation(summary = "新增负载均衡配置")
    public Result<Void> add(@RequestBody LoadBalanceConfig config) {
        loadBalanceConfigService.save(config);
        return Result.ok();
    }

    /**
     * 修改负载均衡配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改负载均衡配置")
    public Result<Void> update(@PathVariable Long id, @RequestBody LoadBalanceConfig config) {
        config.setId(id);
        loadBalanceConfigService.updateById(config);
        return Result.ok();
    }

    /**
     * 删除负载均衡配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除负载均衡配置")
    public Result<Void> delete(@PathVariable Long id) {
        loadBalanceConfigService.removeById(id);
        return Result.ok();
    }

    /**
     * 修改负载均衡配置状态
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "修改负载均衡配置状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        loadBalanceConfigService.updateStatus(id, status);
        return Result.ok();
    }

    /**
     * 根据服务ID查询关联的负载均衡配置
     */
    @GetMapping("/service")
    @Operation(summary = "根据服务ID查询关联的负载均衡配置")
    public Result<List<LoadBalanceConfig>> getLoadBalanceConfigsByServiceId(@RequestParam String serviceId) {
        List<LoadBalanceConfig> configs = loadBalanceConfigService.listByServiceId(serviceId);
        return Result.ok(configs);
    }

    /**
     * 获取负载均衡配置已绑定的服务ID列表
     */
    @GetMapping("/bound-services")
    @Operation(summary = "获取负载均衡配置已绑定的服务ID列表")
    public Result<List<String>> getBoundServices(@RequestParam String configId) {
        List<String> serviceIds = loadBalanceConfigService.getBoundServiceIds(configId);
        return Result.ok(serviceIds);
    }

    /**
     * 绑定服务到负载均衡配置
     */
    @PostMapping("/{configId}/bind-services")
    @Operation(summary = "绑定服务到负载均衡配置")
    public Result<Void> bindServices(@PathVariable String configId, @RequestBody BindServicesRequest request) {
        loadBalanceConfigService.bindServices(configId, request.getServiceIds());
        return Result.ok();
    }

    /**
     * 绑定服务请求体
     */
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
