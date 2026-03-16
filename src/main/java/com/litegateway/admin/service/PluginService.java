package com.litegateway.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.Plugin;

import java.util.List;

public interface PluginService extends IService<Plugin> {

    Plugin getByPluginId(String pluginId);

    List<Plugin> getAllEnabled();

    IPage<Plugin> queryPage(Page<Plugin> page, String pluginName,
                            String pluginType, String executePhase, Integer enabled);

    List<Plugin> getByRouteId(String routeId);

    List<Plugin> getByServiceId(String serviceId);

    Plugin savePlugin(Plugin plugin);

    boolean updatePlugin(Plugin plugin);

    boolean deletePlugin(String pluginId);

    boolean updateStatus(String pluginId, Integer enabled);
}
