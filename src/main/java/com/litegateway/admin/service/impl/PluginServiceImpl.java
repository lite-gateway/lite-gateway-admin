package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.Plugin;
import com.litegateway.admin.repository.mapper.PluginMapper;
import com.litegateway.admin.service.PluginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PluginServiceImpl extends ServiceImpl<PluginMapper, Plugin>
        implements PluginService {

    @Override
    public Plugin getByPluginId(String pluginId) {
        return baseMapper.selectByPluginId(pluginId);
    }

    @Override
    public List<Plugin> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public IPage<Plugin> queryPage(Page<Plugin> page, String pluginName,
                                   String pluginType, String executePhase, Integer enabled) {
        return baseMapper.selectByPage(page, pluginName, pluginType, executePhase, enabled);
    }

    @Override
    public List<Plugin> getByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public List<Plugin> getByServiceId(String serviceId) {
        return baseMapper.selectByServiceId(serviceId);
    }

    @Override
    public Plugin savePlugin(Plugin plugin) {
        if (StringUtils.isBlank(plugin.getPluginId())) {
            plugin.setPluginId("plugin_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        plugin.setCreateTime(LocalDateTime.now());
        plugin.setUpdateTime(LocalDateTime.now());
        if (plugin.getPriority() == null) {
            plugin.setPriority(100);
        }
        if (plugin.getEnabled() == null) {
            plugin.setEnabled(1);
        }
        if (plugin.getTimeout() == null) {
            plugin.setTimeout(5000L);
        }
        if (plugin.getMemoryLimit() == null) {
            plugin.setMemoryLimit(1024);
        }
        if (StringUtils.isBlank(plugin.getVersion())) {
            plugin.setVersion("1.0.0");
        }
        save(plugin);
        return plugin;
    }

    @Override
    public boolean updatePlugin(Plugin plugin) {
        Plugin existing = getByPluginId(plugin.getPluginId());
        if (existing == null) {
            return false;
        }
        plugin.setId(existing.getId());
        plugin.setUpdateTime(LocalDateTime.now());
        return updateById(plugin);
    }

    @Override
    public boolean deletePlugin(String pluginId) {
        Plugin existing = getByPluginId(pluginId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public boolean updateStatus(String pluginId, Integer enabled) {
        Plugin existing = getByPluginId(pluginId);
        if (existing == null) {
            return false;
        }
        existing.setEnabled(enabled);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }
}
