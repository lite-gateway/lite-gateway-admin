package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.Tenant;
import com.litegateway.admin.repository.mapper.TenantMapper;
import com.litegateway.admin.service.TenantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant>
        implements TenantService {

    @Override
    public Tenant getByTenantId(String tenantId) {
        return baseMapper.selectByTenantId(tenantId);
    }

    @Override
    public Tenant getByTenantCode(String tenantCode) {
        return baseMapper.selectByTenantCode(tenantCode);
    }

    @Override
    public List<Tenant> getAllActive() {
        return baseMapper.selectAllActive();
    }

    @Override
    public IPage<Tenant> queryPage(Page<Tenant> page, String tenantName, String tenantCode, String status) {
        return baseMapper.selectByPage(page, tenantName, tenantCode, status);
    }

    @Override
    public List<Tenant> getByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public List<Tenant> getByServiceId(String serviceId) {
        return baseMapper.selectByServiceId(serviceId);
    }

    @Override
    public Tenant saveTenant(Tenant tenant) {
        if (StringUtils.isBlank(tenant.getTenantId())) {
            tenant.setTenantId("tenant_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isBlank(tenant.getStatus())) {
            tenant.setStatus("ACTIVE");
        }
        if (StringUtils.isBlank(tenant.getIsolationLevel())) {
            tenant.setIsolationLevel("SOFT");
        }
        if (tenant.getRateLimit() == null) {
            tenant.setRateLimit(1000);
        }
        if (tenant.getMaxConnections() == null) {
            tenant.setMaxConnections(10000L);
        }
        save(tenant);
        return tenant;
    }

    @Override
    public boolean updateTenant(Tenant tenant) {
        Tenant existing = getByTenantId(tenant.getTenantId());
        if (existing == null) {
            return false;
        }
        tenant.setId(existing.getId());
        tenant.setUpdateTime(LocalDateTime.now());
        return updateById(tenant);
    }

    @Override
    public boolean deleteTenant(String tenantId) {
        Tenant existing = getByTenantId(tenantId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public boolean updateStatus(String tenantId, String status) {
        Tenant existing = getByTenantId(tenantId);
        if (existing == null) {
            return false;
        }
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }
}
