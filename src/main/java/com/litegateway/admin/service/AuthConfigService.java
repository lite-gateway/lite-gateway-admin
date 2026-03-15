package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.AuthConfig;

import java.util.List;

/**
 * 鉴权配置服务接口
 */
public interface AuthConfigService extends IService<AuthConfig> {

    /**
     * 分页查询鉴权配置
     */
    Page<AuthConfig> queryPage(String configName, String authType, Integer status, int pageNum, int pageSize);

    /**
     * 根据配置ID查询
     */
    AuthConfig getByConfigId(String configId);

    /**
     * 查询所有启用的鉴权配置
     */
    List<AuthConfig> listAllEnabled();

    /**
     * 根据鉴权类型查询
     */
    List<AuthConfig> listByAuthType(String authType);

    /**
     * 根据路由ID查询关联的鉴权配置
     */
    List<AuthConfig> listByRouteId(String routeId);

    /**
     * 获取鉴权配置已绑定的路由ID列表
     */
    List<String> getBoundRouteIds(String configId);

    /**
     * 绑定路由到鉴权配置
     */
    void bindRoutes(String configId, List<String> routeIds);

    /**
     * 解绑路由从鉴权配置
     */
    void unbindRoute(String configId, String routeId);

    /**
     * 更新鉴权配置状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 生成唯一的配置ID
     */
    String generateConfigId();
}
