package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.HealthCheckConfig;

import java.util.List;

public interface HealthCheckConfigService extends IService<HealthCheckConfig> {

    Page<HealthCheckConfig> queryPage(String configName, Integer status, int pageNum, int pageSize);

    HealthCheckConfig getByConfigId(String configId);

    List<HealthCheckConfig> listAllEnabled();

    List<HealthCheckConfig> listByServiceId(String serviceId);

    List<String> getBoundServiceIds(String configId);

    void bindServices(String configId, List<String> serviceIds);

    void updateStatus(Long id, Integer status);

    String generateConfigId();
}
