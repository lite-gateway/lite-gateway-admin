package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.TraceConfig;

import java.util.List;

public interface TraceConfigService extends IService<TraceConfig> {

    Page<TraceConfig> queryPage(String configName, Integer status, int pageNum, int pageSize);

    TraceConfig getByConfigId(String configId);

    List<TraceConfig> listAllEnabled();

    List<String> getBoundRouteIds(String configId);

    void bindRoutes(String configId, List<String> routeIds);

    void updateStatus(Long id, Integer status);

    String generateConfigId();
}
