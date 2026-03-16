package com.litegateway.admin.web.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.litegateway.admin.common.web.Result;
import com.litegateway.admin.common.exception.ErrorCode;
import com.litegateway.admin.repository.entity.Tenant;
import com.litegateway.admin.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/gateway/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @GetMapping("/list")
    public Result<IPage<Tenant>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String tenantName,
            @RequestParam(required = false) String tenantCode,
            @RequestParam(required = false) String status) {
        Page<Tenant> pageParam = new Page<>(page, size);
        IPage<Tenant> result = tenantService.queryPage(pageParam, tenantName, tenantCode, status);
        return Result.success(result);
    }

    @GetMapping("/{tenantId}")
    public Result<Tenant> getById(@PathVariable String tenantId) {
        Tenant tenant = tenantService.getByTenantId(tenantId);
        if (tenant == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(tenant);
    }

    @GetMapping("/code/{tenantCode}")
    public Result<Tenant> getByCode(@PathVariable String tenantCode) {
        Tenant tenant = tenantService.getByTenantCode(tenantCode);
        if (tenant == null) {
            return Result.fail(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return Result.success(tenant);
    }

    @PostMapping
    public Result<Tenant> create(@RequestBody Tenant tenant) {
        Tenant saved = tenantService.saveTenant(tenant);
        return Result.success(saved);
    }

    @PutMapping("/{tenantId}")
    public Result<Tenant> update(@PathVariable String tenantId, @RequestBody Tenant tenant) {
        tenant.setTenantId(tenantId);
        boolean success = tenantService.updateTenant(tenant);
        if (success) {
            return Result.success(tenant);
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @DeleteMapping("/{tenantId}")
    public Result<Void> delete(@PathVariable String tenantId) {
        boolean success = tenantService.deleteTenant(tenantId);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @PutMapping("/{tenantId}/status")
    public Result<Void> updateStatus(@PathVariable String tenantId, @RequestParam String status) {
        boolean success = tenantService.updateStatus(tenantId, status);
        if (success) {
            return Result.success();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }

    @GetMapping("/active/all")
    public Result<List<Tenant>> getAllActive() {
        List<Tenant> list = tenantService.getAllActive();
        return Result.success(list);
    }

    @GetMapping("/isolation-levels")
    public Result<List<String>> getIsolationLevels() {
        return Result.success(Arrays.asList("SOFT", "HARD"));
    }

    @GetMapping("/status-list")
    public Result<List<String>> getStatusList() {
        return Result.success(Arrays.asList("ACTIVE", "INACTIVE", "SUSPENDED", "EXPIRED"));
    }
}
