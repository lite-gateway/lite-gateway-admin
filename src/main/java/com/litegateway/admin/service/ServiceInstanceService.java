package com.litegateway.admin.service;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.baomidou.mybatisplus.extension.service.IService;
import com.litegateway.admin.repository.entity.ServiceInstance;

import java.util.List;

/**
 * 服务实例服务接口
 * 统一管理服务实例，包括本地数据库和Nacos同步
 */
public interface ServiceInstanceService extends IService<ServiceInstance> {

    // ==================== 本地数据库操作 ====================

    /**
     * 根据服务ID查询实例列表
     */
    List<ServiceInstance> listByServiceId(Long serviceId);

    /**
     * 根据服务名查询实例列表
     */
    List<ServiceInstance> listByServiceName(String serviceName);

    /**
     * 根据实例ID查询
     */
    ServiceInstance getByInstanceId(String instanceId);

    /**
     * 获取服务实例统计
     */
    ServiceInstanceStats getInstanceStats(Long serviceId);

    // ==================== Nacos同步操作 ====================

    /**
     * 从Nacos同步指定服务的实例到本地数据库
     * @param serviceId 服务ID
     * @param serviceName 服务名称
     */
    void syncInstancesFromNacos(Long serviceId, String serviceName);

    /**
     * 从Nacos同步所有服务的实例到本地数据库
     */
    void syncAllInstancesFromNacos();

    /**
     * 从Nacos查询实例列表（不存储到数据库）
     * @param serviceName 服务名称
     * @return Nacos中的实例列表
     */
    List<Instance> getInstancesFromNacos(String serviceName);

    /**
     * 分页从Nacos查询实例列表
     * @param serviceName 服务名称
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Nacos中的实例列表
     */
    List<Instance> getInstancesFromNacosPage(String serviceName, int pageNum, int pageSize);

    // ==================== 实例管理（同时更新Nacos和数据库） ====================

    /**
     * 更新实例权重（同时更新Nacos和数据库）
     */
    void updateWeight(Long id, Double weight);

    /**
     * 更新实例启用状态（同时更新Nacos和数据库）
     */
    void updateEnabled(Long id, Boolean enabled);

    /**
     * 实例统计类
     */
    class ServiceInstanceStats {
        private int totalCount;
        private int healthyCount;
        private int enabledCount;

        public ServiceInstanceStats(int totalCount, int healthyCount, int enabledCount) {
            this.totalCount = totalCount;
            this.healthyCount = healthyCount;
            this.enabledCount = enabledCount;
        }

        public int getTotalCount() { return totalCount; }
        public int getHealthyCount() { return healthyCount; }
        public int getEnabledCount() { return enabledCount; }
    }
}
