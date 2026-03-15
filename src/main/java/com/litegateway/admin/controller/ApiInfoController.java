package com.litegateway.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.PageBody;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.repository.entity.ApiInfo;
import com.litegateway.admin.repository.entity.ServiceInfo;
import com.litegateway.admin.service.ApiInfoService;
import com.litegateway.admin.service.ServiceInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API管理控制器
 * 仅在 Nacos 启用时加载
 */
@Slf4j
@RestController
@RequestMapping("/gateway/api")
@RequiredArgsConstructor
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(com.alibaba.nacos.api.naming.NamingService.class)
public class ApiInfoController {

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired(required = false)
    private ServiceInfoService serviceInfoService;

    /**
     * 分页查询API列表
     */
    @GetMapping("/page")
    public Result<PageBody<ApiInfo>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Integer status) {

        Page<ApiInfo> page = apiInfoService.queryPage(path, method, serviceId, status, pageNum, pageSize);

        PageBody<ApiInfo> pageBody = new PageBody<>();
        pageBody.setList(page.getRecords());
        pageBody.setTotal(page.getTotal());
        pageBody.setPages((int) page.getPages());
        pageBody.setPageSize((int) page.getSize());
        pageBody.setPageNum((int) page.getCurrent());

        return Result.ok(pageBody);
    }

    /**
     * 查询所有已发布的API
     */
    @GetMapping("/list")
    public Result<List<ApiInfo>> listPublished() {
        List<ApiInfo> list = apiInfoService.listPublished();
        return Result.ok(list);
    }

    /**
     * 根据服务ID查询API列表
     */
    @GetMapping("/service/{serviceId}")
    public Result<List<ApiInfo>> listByServiceId(@PathVariable Long serviceId) {
        List<ApiInfo> list = apiInfoService.listByServiceId(serviceId);
        return Result.ok(list);
    }

    /**
     * 根据ID查询API详情
     */
    @GetMapping("/{id}")
    public Result<ApiInfo> getById(@PathVariable Long id) {
        ApiInfo api = apiInfoService.getById(id);
        return Result.ok(api);
    }

    /**
     * 新增API
     */
    @PostMapping
    public Result<Void> add(@RequestBody ApiInfo api) {
        apiInfoService.save(api);
        return Result.ok();
    }

    /**
     * 修改API
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ApiInfo api) {
        api.setId(id);
        apiInfoService.updateById(api);
        return Result.ok();
    }

    /**
     * 删除API
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        apiInfoService.removeById(id);
        return Result.ok();
    }

    /**
     * 发布API
     */
    @PostMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        apiInfoService.publish(id);
        return Result.ok();
    }

    /**
     * 下线API
     */
    @PostMapping("/{id}/offline")
    public Result<Void> offline(@PathVariable Long id) {
        apiInfoService.offline(id);
        return Result.ok();
    }

    /**
     * 从Swagger导入API
     */
    @PostMapping("/import/swagger")
    public Result<List<ApiInfo>> importFromSwagger(
            @RequestParam String swaggerUrl,
            @RequestParam Long serviceId) {
        List<ApiInfo> apis = apiInfoService.importFromSwagger(swaggerUrl, serviceId);
        return Result.ok(apis);
    }

    /**
     * 关联API到路由
     */
    @PostMapping("/{id}/bind-route")
    public Result<Void> bindRoute(@PathVariable Long id, @RequestParam Long routeId) {
        apiInfoService.bindRoute(id, routeId);
        return Result.ok();
    }

    /**
     * 获取服务列表（用于下拉选择）
     */
    @GetMapping("/services")
    public Result<List<ServiceInfo>> listServices() {
        List<ServiceInfo> list = serviceInfoService.listAllOnline();
        return Result.ok(list);
    }
}
