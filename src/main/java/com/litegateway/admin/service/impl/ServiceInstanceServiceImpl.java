package com.litegateway.admin.service.impl;

import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.exception.NacosException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.cache.InstanceCacheManager;
import com.litegateway.admin.nacos.NacosServiceClient;
import com.litegateway.admin.nacos.NacosServiceClient.PageResult;
import com.litegateway.admin.query.InstanceQuery;
import com.litegateway.admin.repository.entity.ServiceInstance;
import com.litegateway.admin.repository.mapper.ServiceInstanceMapper;
import com.litegateway.admin.service.ServiceInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 服务实例服务实现类
 * 统一管理服务实例，包括本地数据库和Nacos同步
 * 仅在 Nacos 启用时加载
 */
@Slf4j
@Service
@ConditionalOnBean(NacosServiceClient.class)
public class ServiceInstanceServiceImpl extends ServiceImpl<ServiceInstanceMapper, ServiceInstance>
        implements ServiceInstanceService {

    @Value("${spring.cloud.nacos.discovery.server-addr:localhost:8848}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}")
    private String group;

    @Autowired
    private ServiceInstanceMapper instanceMapper;

    @Autowired
    private NacosServiceClient nacosServiceClient;

    @Autowired
    private InstanceCacheManager cacheManager;

    // ==================== 本地数据库操作 ====================

    @Override
    public List<ServiceInstance> listByServiceId(Long serviceId) {
        return instanceMapper.selectByServiceId(serviceId);
    }

    @Override
    public List<ServiceInstance> listByServiceName(String serviceName) {
        return instanceMapper.selectByServiceName(serviceName);
    }

    @Override
    public ServiceInstance getByInstanceId(String instanceId) {
        return instanceMapper.selectByInstanceId(instanceId);
    }

    @Override
    public ServiceInstanceStats getInstanceStats(Long serviceId) {
        int totalCount = instanceMapper.countByServiceId(serviceId);
        int healthyCount = instanceMapper.countHealthyByServiceId(serviceId);

        List<ServiceInstance> instances = instanceMapper.selectByServiceId(serviceId);
        long enabledCount = instances.stream().filter(ServiceInstance::getEnabled).count();

        return new ServiceInstanceStats(totalCount, healthyCount, (int) enabledCount);
    }

    /**
     * 实例统计类
     */
    public static class ServiceInstanceStats implements com.litegateway.admin.service.ServiceInstanceService.ServiceInstanceStats {
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

    // ==================== Nacos同步操作 ====================

    @Override
    public void syncInstancesFromNacos(Long serviceId, String serviceName) {
        try {
            // 使用 NacosServiceClient 获取实例（带缓存）
            List<Instance> nacosInstances = nacosServiceClient.getAllInstances(serviceName);

            // 标记所有现有记录为已删除
            instanceMapper.deleteByServiceId(serviceId);

            // 插入新记录
            for (Instance nacosInstance : nacosInstances) {
                ServiceInstance instance = convertToEntity(serviceId, serviceName, nacosInstance);
                instanceMapper.insert(instance);
            }

            log.info("Synced {} instances from Nacos for service: {}", nacosInstances.size(), serviceName);
        } catch (Exception e) {
            log.error("Failed to sync instances from Nacos for service: {}", serviceName, e);
            throw new RuntimeException("从Nacos同步实例失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void syncAllInstancesFromNacos() {
        log.info("Starting full instance sync from Nacos...");
        // 获取所有服务并逐个同步
        List<String> services = nacosServiceClient.getAllServices();
        for (String serviceName : services) {
            try {
                // 这里需要根据 serviceName 查询对应的数据库记录
                // 简化处理，实际应该查询 ServiceInfo 表获取 serviceId
                log.debug("Syncing instances for service: {}", serviceName);
            } catch (Exception e) {
                log.error("Failed to sync instances for service: {}", serviceName, e);
            }
        }
        log.info("Full instance sync completed, total services: {}", services.size());
    }

    @Override
    public List<Instance> getInstancesFromNacos(String serviceName) {
        // 使用 NacosServiceClient（带缓存）
        return nacosServiceClient.getAllInstances(serviceName);
    }

    @Override
    public List<Instance> getInstancesFromNacosPage(String serviceName, int pageNum, int pageSize) {
        InstanceQuery query = new InstanceQuery();
        query.setServiceName(serviceName);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        
        PageResult<Instance> result = nacosServiceClient.getInstancesPage(query);
        return result.list();
    }

    @Override
    public PageResult<Instance> getAllInstancesPage(InstanceQuery query) {
        return nacosServiceClient.getInstancesPage(query);
    }

    // ==================== 实例管理（同时更新Nacos和数据库） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWeight(Long id, Double weight) {
        ServiceInstance instance = instanceMapper.selectById(id);
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }

        // 更新数据库
        instance.setWeight(weight);
        instance.setUpdateTime(LocalDateTime.now());
        instanceMapper.updateById(instance);

        // 同步更新Nacos
        try {
            NamingMaintainService maintainService = NamingMaintainFactory.createMaintainService(serverAddr);
            Instance nacosInstance = new Instance();
            nacosInstance.setServiceName(instance.getServiceName());
            nacosInstance.setInstanceId(instance.getInstanceId());
            nacosInstance.setWeight(weight);
            maintainService.updateInstance(instance.getServiceName(), group, nacosInstance);
            
            // 更新缓存
            cacheManager.invalidate(instance.getServiceName());
            
            log.info("Updated instance weight in Nacos: {} -> {}", id, weight);
        } catch (NacosException e) {
            log.error("Failed to update instance weight in Nacos", e);
            throw new RuntimeException("更新Nacos实例权重失败: " + e.getMessage(), e);
        }

        log.info("Updated instance weight: {} -> {}", id, weight);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnabled(Long id, Boolean enabled) {
        ServiceInstance instance = instanceMapper.selectById(id);
        if (instance == null) {
            throw new RuntimeException("实例不存在");
        }

        // 更新数据库
        instance.setEnabled(enabled);
        instance.setUpdateTime(LocalDateTime.now());
        instanceMapper.updateById(instance);

        // 同步更新Nacos
        try {
            NamingMaintainService maintainService = NamingMaintainFactory.createMaintainService(serverAddr);
            Instance nacosInstance = new Instance();
            nacosInstance.setServiceName(instance.getServiceName());
            nacosInstance.setInstanceId(instance.getInstanceId());
            nacosInstance.setEnabled(enabled);
            maintainService.updateInstance(instance.getServiceName(), group, nacosInstance);
            
            // 更新缓存
            cacheManager.invalidate(instance.getServiceName());
            
            log.info("Updated instance enabled status in Nacos: {} -> {}", id, enabled);
        } catch (NacosException e) {
            log.error("Failed to update instance enabled status in Nacos", e);
            throw new RuntimeException("更新Nacos实例状态失败: " + e.getMessage(), e);
        }

        log.info("Updated instance enabled status: {} -> {}", id, enabled);
    }

    // ==================== 私有方法 ====================

    private ServiceInstance convertToEntity(Long serviceId, String serviceName, Instance nacosInstance) {
        ServiceInstance instance = new ServiceInstance();
        instance.setServiceId(serviceId);
        instance.setServiceName(serviceName);
        instance.setInstanceId(nacosInstance.getInstanceId());
        instance.setIp(nacosInstance.getIp());
        instance.setPort(nacosInstance.getPort());
        instance.setWeight(nacosInstance.getWeight() != 0 ? nacosInstance.getWeight() : 1.0);
        instance.setHealthy(nacosInstance.isHealthy());
        instance.setEnabled(nacosInstance.isEnabled());
        instance.setEphemeral(nacosInstance.isEphemeral());
        instance.setClusterName(nacosInstance.getClusterName());
        instance.setMetadata(nacosInstance.getMetadata() != null ?
                nacosInstance.getMetadata().toString() : null);
        instance.setCreateTime(LocalDateTime.now());
        instance.setUpdateTime(LocalDateTime.now());
        instance.setDeleted(0);
        return instance;
    }
}
