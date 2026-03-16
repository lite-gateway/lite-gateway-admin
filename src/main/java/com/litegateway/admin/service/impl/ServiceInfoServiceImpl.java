package com.litegateway.admin.service.impl;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.ServiceInfo;
import com.litegateway.admin.repository.entity.ServiceInstance;
import com.litegateway.admin.repository.mapper.ServiceInfoMapper;
import com.litegateway.admin.repository.mapper.ServiceInstanceMapper;
import com.litegateway.admin.service.ServiceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

/**
 * 服务信息服务实现类
 */
@Slf4j
@Service
public class ServiceInfoServiceImpl extends ServiceImpl<ServiceInfoMapper, ServiceInfo>
        implements ServiceInfoService {

    @Autowired(required = false)
    private NamingService namingService;

    @Value("${spring.cloud.nacos.discovery.server-addr:localhost:8848}")
    private String serverAddr;

    @Value("${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}")
    private String group;

    @Autowired
    private ServiceInstanceMapper instanceMapper;

    @Override
    public Page<ServiceInfo> queryPage(String serviceName, String groupName, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<ServiceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ServiceInfo::getDeleted, 0);

        if (StringUtils.isNotBlank(serviceName)) {
            wrapper.like(ServiceInfo::getServiceName, serviceName)
                    .or()
                    .like(ServiceInfo::getDisplayName, serviceName);
        }
        if (StringUtils.isNotBlank(groupName)) {
            wrapper.eq(ServiceInfo::getGroupName, groupName);
        }
        if (status != null) {
            wrapper.eq(ServiceInfo::getStatus, status);
        }

        wrapper.orderByDesc(ServiceInfo::getCreateTime);

        Page<ServiceInfo> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public ServiceInfo getByServiceName(String serviceName) {
        return baseMapper.selectByServiceName(serviceName);
    }

    @Override
    public List<ServiceInfo> listAllOnline() {
        return baseMapper.selectAllOnline();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncFromNacos() {
        if (namingService == null) {
            log.warn("NamingService is not available, skip sync from Nacos");
            return;
        }
        log.info("Starting to sync services from Nacos...");
        try {
            // 获取Nacos中的所有服务
            ListView<String> services = namingService.getServicesOfServer(1, Integer.MAX_VALUE);
            List<String> serviceNames = services.getData();

            log.info("Found {} services from Nacos", serviceNames.size());

            for (String serviceName : serviceNames) {
                try {
                    syncSingleService(serviceName);
                } catch (Exception e) {
                    log.error("Failed to sync service: {}", serviceName, e);
                }
            }

            log.info("Service sync completed");
        } catch (Exception e) {
            log.error("Failed to sync services from Nacos", e);
            throw new RuntimeException("同步Nacos服务失败", e);
        }
    }

    private void syncSingleService(String serviceName) throws Exception {
        // 查询现有服务
        ServiceInfo serviceInfo = baseMapper.selectByServiceName(serviceName);

        if (serviceInfo == null) {
            // 创建新服务
            serviceInfo = new ServiceInfo();
            serviceInfo.setServiceName(serviceName);
            serviceInfo.setDisplayName(serviceName);
            serviceInfo.setGroupName("DEFAULT_GROUP");
            serviceInfo.setNamespaceId("public");
            serviceInfo.setProtocol("http");
            serviceInfo.setStatus(1);
            serviceInfo.setSyncedFromNacos(true);
            serviceInfo.setCreateTime(LocalDateTime.now());
            serviceInfo.setUpdateTime(LocalDateTime.now());
            baseMapper.insert(serviceInfo);
            log.info("Created new service: {}", serviceName);
        } else {
            // 更新同步时间
            serviceInfo.setLastSyncTime(LocalDateTime.now());
            serviceInfo.setSyncedFromNacos(true);
            baseMapper.updateById(serviceInfo);
        }

        // 同步实例
        syncServiceInstances(serviceInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncServiceInstances(String serviceName) {
        ServiceInfo serviceInfo = baseMapper.selectByServiceName(serviceName);
        if (serviceInfo == null) {
            log.warn("Service not found: {}", serviceName);
            return;
        }
        syncServiceInstances(serviceInfo);
    }

    private void syncServiceInstances(ServiceInfo serviceInfo) {
        if (namingService == null) {
            log.warn("NamingService is not available, skip sync service instances");
            return;
        }
        try {
            // 获取Nacos中的实例列表
            List<Instance> instances = namingService.selectInstances(serviceInfo.getServiceName(), true);

            // 删除旧实例
            instanceMapper.deleteByServiceId(serviceInfo.getId());

            int healthyCount = 0;

            // 插入新实例
            for (Instance instance : instances) {
                ServiceInstance serviceInstance = convertToServiceInstance(instance);
                serviceInstance.setServiceId(serviceInfo.getId());
                serviceInstance.setServiceName(serviceInfo.getServiceName());
                serviceInstance.setCreateTime(LocalDateTime.now());
                serviceInstance.setUpdateTime(LocalDateTime.now());
                instanceMapper.insert(serviceInstance);

                if (instance.isHealthy()) {
                    healthyCount++;
                }
            }

            // 更新服务实例统计
            serviceInfo.setInstanceCount(instances.size());
            serviceInfo.setHealthyInstanceCount(healthyCount);
            serviceInfo.setLastSyncTime(LocalDateTime.now());

            // 更新服务状态
            if (instances.isEmpty()) {
                serviceInfo.setStatus(0); // 离线
            } else if (healthyCount < instances.size()) {
                serviceInfo.setStatus(2); // 部分离线
            } else {
                serviceInfo.setStatus(1); // 在线
            }

            baseMapper.updateById(serviceInfo);

            log.info("Synced {} instances for service: {}", instances.size(), serviceInfo.getServiceName());
        } catch (Exception e) {
            log.error("Failed to sync instances for service: {}", serviceInfo.getServiceName(), e);
        }
    }

    private ServiceInstance convertToServiceInstance(Instance instance) {
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setInstanceId(instance.getInstanceId());
        serviceInstance.setIp(instance.getIp());
        serviceInstance.setPort(instance.getPort());
        serviceInstance.setWeight(instance.getWeight());
        serviceInstance.setHealthy(instance.isHealthy());
        serviceInstance.setEnabled(instance.isEnabled());
        serviceInstance.setEphemeral(instance.isEphemeral());
        serviceInstance.setClusterName(instance.getClusterName());

        // 元数据转JSON
        if (instance.getMetadata() != null) {
            serviceInstance.setMetadata(instance.getMetadata().toString());
        }

        return serviceInstance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSwaggerUrl(Long id, String swaggerUrl) {
        ServiceInfo serviceInfo = baseMapper.selectById(id);
        if (serviceInfo == null) {
            throw new RuntimeException("服务不存在");
        }
        serviceInfo.setSwaggerUrl(swaggerUrl);
        serviceInfo.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(serviceInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMetadata(Long id, String metadata) {
        ServiceInfo serviceInfo = baseMapper.selectById(id);
        if (serviceInfo == null) {
            throw new RuntimeException("服务不存在");
        }
        serviceInfo.setMetadata(metadata);
        serviceInfo.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(serviceInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ServiceInfo entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getSyncedFromNacos() == null) {
            entity.setSyncedFromNacos(false);
        }
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        return super.save(entity);
    }
}
