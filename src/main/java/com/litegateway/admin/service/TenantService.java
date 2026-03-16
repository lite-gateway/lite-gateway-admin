package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.Tenant;

import java.util.List;

public interface TenantService extends IService<Tenant> {

    Tenant getByTenantId(String tenantId);

    Tenant getByTenantCode(String tenantCode);

    List<Tenant> getAllActive();

    IPage<Tenant> queryPage(Page<Tenant> page, String tenantName, String tenantCode, String status);

    List<Tenant> getByRouteId(String routeId);

    List<Tenant> getByServiceId(String serviceId);

    Tenant saveTenant(Tenant tenant);

    boolean updateTenant(Tenant tenant);

    boolean deleteTenant(String tenantId);

    boolean updateStatus(String tenantId, String status);
}
