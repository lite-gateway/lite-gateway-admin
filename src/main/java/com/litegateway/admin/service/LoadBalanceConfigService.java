package com.litegateway.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.LoadBalanceConfig;

import java.util.List;

/**
 * 负载均衡配置服务接口
 */
public interface LoadBalanceConfigService extends IService<LoadBalanceConfig> {

    /**
     * 分页查询负载均衡配置
     */
    Page<LoadBalanceConfig> queryPage(String configName, String strategy, Integer status, int pageNum, int pageSize);

    /**
     * 根据配置ID查询
     */
    LoadBalanceConfig getByConfigId(String configId);

    /**
     * 查询所有启用的负载均衡配置
     */
    List<LoadBalanceConfig> listAllEnabled();

    /**
     * 根据策略类型查询
     */
    List<LoadBalanceConfig> listByStrategy(String strategy);

    /**
     * 根据服务ID查询关联的负载均衡配置
     */
    List<LoadBalanceConfig> listByServiceId(String serviceId);

    /**
     * 获取负载均衡配置已绑定的服务ID列表
     */
    List<String> getBoundServiceIds(String configId);

    /**
     * 绑定服务到负载均衡配置
     */
    void bindServices(String configId, List<String> serviceIds);

    /**
     * 解绑服务从负载均衡配置
     */
    void unbindService(String configId, String serviceId);

    /**
     * 更新负载均衡配置状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 生成唯一的配置ID
     */
    String generateConfigId();
}
