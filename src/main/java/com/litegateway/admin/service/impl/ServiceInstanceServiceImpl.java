package com.litegateway.admin.service.impl;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.exception.NacosException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litegateway.admin.repository.entity.ServiceInstance;
import com.litegateway.admin.repository.mapper.ServiceInstanceMapper;
import com.litegateway.admin.service.ServiceInstanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务实例服务实现类
 * 统一管理服务实例，包括本地数据库和Nacos同步
 */
@Slf4j
@Service
public class ServiceInstanceServiceImpl extends ServiceImpl<ServiceInstanceMapper, ServiceInstance>
        implements ServiceInstanceService {

    @Value("${spring.cloud.nacos.config.server-addr:localhost:8848}")
    private String serverAddr;

    @Value("${spring.profiles.active:local}")
    private String group;

    private final String clusterName = "DEFAULT";

    @Autowired
    private ServiceInstanceMapper instanceMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

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

    // ==================== Nacos同步操作 ====================

    @Override
    public void syncInstancesFromNacos(Long serviceId, String serviceName) {
        try {
            NamingService naming = NamingFactory.createNamingService(serverAddr);
            List<Instance> nacosInstances = naming.getAllInstances(serviceName, group);

            // 标记所有现有记录为已删除
            instanceMapper.deleteByServiceId(serviceId);

            // 插入新记录
            for (Instance nacosInstance : nacosInstances) {
                ServiceInstance instance = convertToEntity(serviceId, serviceName, nacosInstance);
                instanceMapper.insert(instance);
            }

            log.info("Synced {} instances from Nacos for service: {}", nacosInstances.size(), serviceName);
        } catch (NacosException e) {
            log.error("Failed to sync instances from Nacos for service: {}", serviceName, e);
            throw new RuntimeException("从Nacos同步实例失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void syncAllInstancesFromNacos() {
        log.info("Starting full instance sync from Nacos...");
        // TODO: 获取所有服务并逐个同步
        log.info("Full instance sync completed");
    }

    @Override
    public List<Instance> getInstancesFromNacos(String serviceName) {
        try {
            NamingService naming = NamingFactory.createNamingService(serverAddr);
            return naming.getAllInstances(serviceName, group);
        } catch (NacosException e) {
            log.error("Failed to get instances from Nacos for service: {}", serviceName, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Instance> getInstancesFromNacosPage(String serviceName, int pageNum, int pageSize) {
        String serviceNameWithGroup = group + "@@" + serviceName;
        String url = String.format("http://%s/nacos/v1/ns/catalog/instances?serviceName=%s&clusterName=%s&namespaceId=%s&pageSize=%d&pageNo=%d",
                serverAddr, serviceNameWithGroup, clusterName, group, pageSize, pageNum);

        log.debug("Query Nacos instances: {}", url);

        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response == null || response.isEmpty()) {
                return new ArrayList<>();
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode listNode = root.get("list");

            if (listNode != null && listNode.isArray()) {
                List<Instance> instances = new ArrayList<>();
                for (JsonNode node : listNode) {
                    Instance instance = objectMapper.treeToValue(node, Instance.class);
                    instances.add(instance);
                }
                return instances;
            }
        } catch (Exception e) {
            log.error("Failed to get instances page from Nacos for service: {}", serviceName, e);
        }
        return new ArrayList<>();
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
