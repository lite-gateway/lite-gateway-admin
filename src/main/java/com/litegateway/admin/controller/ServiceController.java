package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.ServiceInfo;
import com.litegateway.admin.repository.entity.ServiceInstance;
import com.litegateway.admin.service.ServiceInfoService;
import com.litegateway.admin.service.ServiceInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/gateway/service")
@RequiredArgsConstructor
public class ServiceController {

    @Autowired
    private ServiceInfoService serviceInfoService;

    @Autowired
    private ServiceInstanceService serviceInstanceService;

    /**
     * 分页查询服务列表
     */
    @GetMapping("/page")
    public Result<PageBody<ServiceInfo>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String groupName,
            @RequestParam(required = false) Integer status) {

        Page<ServiceInfo> page = serviceInfoService.queryPage(serviceName, groupName, status, pageNum, pageSize);

        PageBody<ServiceInfo> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有在线服务（用于下拉选择）
     */
    @GetMapping("/list")
    public Result<List<ServiceInfo>> listAllOnline() {
        List<ServiceInfo> list = serviceInfoService.listAllOnline();
        return Result.ok(list);
    }

    /**
     * 根据服务名查询
     */
    @GetMapping("/name/{serviceName}")
    public Result<ServiceInfo> getByServiceName(@PathVariable String serviceName) {
        ServiceInfo service = serviceInfoService.getByServiceName(serviceName);
        return Result.ok(service);
    }

    /**
     * 根据ID查询服务详情
     */
    @GetMapping("/{id}")
    public Result<ServiceInfo> getById(@PathVariable Long id) {
        ServiceInfo service = serviceInfoService.getById(id);
        return Result.ok(service);
    }

    /**
     * 新增服务
     */
    @PostMapping
    public Result<Void> add(@RequestBody ServiceInfo service) {
        serviceInfoService.save(service);
        return Result.ok();
    }

    /**
     * 修改服务
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ServiceInfo service) {
        service.setId(id);
        serviceInfoService.updateById(service);
        return Result.ok();
    }

    /**
     * 删除服务
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        serviceInfoService.removeById(id);
        return Result.ok();
    }

    /**
     * 从Nacos同步服务列表
     */
    @PostMapping("/sync/nacos")
    public Result<Void> syncFromNacos() {
        serviceInfoService.syncFromNacos();
        return Result.ok();
    }

    /**
     * 同步指定服务的实例信息
     */
    @PostMapping("/{serviceName}/sync-instances")
    public Result<Void> syncServiceInstances(@PathVariable String serviceName) {
        serviceInfoService.syncServiceInstances(serviceName);
        return Result.ok();
    }

    /**
     * 更新Swagger URL
     */
    @PutMapping("/{id}/swagger-url")
    public Result<Void> updateSwaggerUrl(@PathVariable Long id, @RequestParam String swaggerUrl) {
        serviceInfoService.updateSwaggerUrl(id, swaggerUrl);
        return Result.ok();
    }

    // ==================== 实例管理接口 ====================

    /**
     * 查询服务的实例列表
     */
    @GetMapping("/{serviceId}/instances")
    public Result<List<ServiceInstance>> listInstances(@PathVariable Long serviceId) {
        List<ServiceInstance> instances = serviceInstanceService.listByServiceId(serviceId);
        return Result.ok(instances);
    }

    /**
     * 查询服务实例统计
     */
    @GetMapping("/{serviceId}/instance-stats")
    public Result<ServiceInstanceService.ServiceInstanceStats> getInstanceStats(@PathVariable Long serviceId) {
        ServiceInstanceService.ServiceInstanceStats stats = serviceInstanceService.getInstanceStats(serviceId);
        return Result.ok(stats);
    }

    /**
     * 更新实例权重
     */
    @PutMapping("/instances/{id}/weight")
    public Result<Void> updateInstanceWeight(@PathVariable Long id, @RequestParam Double weight) {
        serviceInstanceService.updateWeight(id, weight);
        return Result.ok();
    }

    /**
     * 更新实例启用状态
     */
    @PutMapping("/instances/{id}/enabled")
    public Result<Void> updateInstanceEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        serviceInstanceService.updateEnabled(id, enabled);
        return Result.ok();
    }
}
