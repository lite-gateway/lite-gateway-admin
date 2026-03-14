package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.GatewayFeatureConfig;

import java.util.List;

/**
 * 功能配置服务接口
 */
public interface GatewayFeatureConfigService extends IService<GatewayFeatureConfig> {

    /**
     * 分页查询功能配置
     */
    Page<GatewayFeatureConfig> queryPage(String featureName, Boolean enabled, int pageNum, int pageSize);

    /**
     * 根据功能编码查询
     */
    GatewayFeatureConfig getByFeatureCode(String featureCode);

    /**
     * 查询所有启用的功能配置
     */
    List<GatewayFeatureConfig> listAllEnabled();

    /**
     * 根据路由ID查询匹配的功能配置
     */
    List<GatewayFeatureConfig> listByRouteId(String routeId);

    /**
     * 更新功能状态
     */
    void updateStatus(Long id, Boolean enabled);

    /**
     * 批量更新功能状态
     */
    void batchUpdateStatus(List<Long> ids, Boolean enabled);
}
