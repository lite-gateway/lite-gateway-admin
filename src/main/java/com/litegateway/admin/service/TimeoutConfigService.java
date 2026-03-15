package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.TimeoutConfig;

import java.util.List;

/**
 * 超时配置服务接口
 */
public interface TimeoutConfigService extends IService<TimeoutConfig> {

    /**
     * 分页查询超时配置
     */
    Page<TimeoutConfig> queryPage(String configName, String configType, Integer status, int pageNum, int pageSize);

    /**
     * 根据配置ID查询
     */
    TimeoutConfig getByConfigId(String configId);

    /**
     * 查询所有启用的超时配置
     */
    List<TimeoutConfig> listAllEnabled();

    /**
     * 根据配置类型查询
     */
    List<TimeoutConfig> listByConfigType(String configType);

    /**
     * 获取全局配置
     */
    TimeoutConfig getGlobalConfig();

    /**
     * 根据路由ID查询关联的超时配置
     */
    List<TimeoutConfig> listByRouteId(String routeId);

    /**
     * 获取超时配置已绑定的路由ID列表
     */
    List<String> getBoundRouteIds(String configId);

    /**
     * 绑定路由到超时配置
     */
    void bindRoutes(String configId, List<String> routeIds);

    /**
     * 解绑路由从超时配置
     */
    void unbindRoute(String configId, String routeId);

    /**
     * 更新超时配置状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 生成唯一的配置ID
     */
    String generateConfigId();
}
