package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.HealthCheckConfig;
import com.litegateway.admin.repository.entity.HealthCheckServiceRelation;
import com.litegateway.admin.repository.mapper.HealthCheckConfigMapper;
import com.litegateway.admin.repository.mapper.HealthCheckServiceRelationMapper;
import com.litegateway.admin.service.HealthCheckConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HealthCheckConfigServiceImpl extends ServiceImpl<HealthCheckConfigMapper, HealthCheckConfig>
        implements HealthCheckConfigService {

    @Autowired
    private HealthCheckServiceRelationMapper relationMapper;

    @Override
    public Page<HealthCheckConfig> queryPage(String configName, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<HealthCheckConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthCheckConfig::getDeleted, 0);

        if (StringUtils.isNotBlank(configName)) {
            wrapper.like(HealthCheckConfig::getConfigName, configName);
        }
        if (status != null) {
            wrapper.eq(HealthCheckConfig::getStatus, status);
        }

        wrapper.orderByDesc(HealthCheckConfig::getCreateTime);

        Page<HealthCheckConfig> page = new Page<>(pageNum, pageSize);
        Page<HealthCheckConfig> resultPage = baseMapper.selectPage(page, wrapper);

        resultPage.getRecords().forEach(config -> {
            List<String> serviceIds = relationMapper.selectServiceIdsByConfigId(config.getConfigId());
            config.setServiceIds(serviceIds);
        });

        return resultPage;
    }

    @Override
    public HealthCheckConfig getByConfigId(String configId) {
        HealthCheckConfig config = baseMapper.selectByConfigId(configId);
        if (config != null) {
            List<String> serviceIds = relationMapper.selectServiceIdsByConfigId(configId);
            config.setServiceIds(serviceIds);
        }
        return config;
    }

    @Override
    public List<HealthCheckConfig> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<HealthCheckConfig> listByServiceId(String serviceId) {
        List<String> configIds = relationMapper.selectConfigIdsByServiceId(serviceId);
        if (configIds.isEmpty()) {
            return List.of();
        }
        return baseMapper.selectBatchIds(configIds.stream().map(Long::valueOf).collect(Collectors.toList()));
    }

    @Override
    public List<String> getBoundServiceIds(String configId) {
        return relationMapper.selectServiceIdsByConfigId(configId);
    }

    @Override
    @Transactional
    public void bindServices(String configId, List<String> serviceIds) {
        relationMapper.deleteByConfigId(configId);

        if (serviceIds != null && !serviceIds.isEmpty()) {
            List<HealthCheckServiceRelation> relations = serviceIds.stream()
                    .map(serviceId -> {
                        HealthCheckServiceRelation relation = new HealthCheckServiceRelation();
                        relation.setConfigId(configId);
                        relation.setServiceId(serviceId);
                        relation.setCreateTime(LocalDateTime.now());
                        return relation;
                    })
                    .collect(Collectors.toList());

            relations.forEach(relationMapper::insert);
        }
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        HealthCheckConfig config = new HealthCheckConfig();
        config.setId(id);
        config.setStatus(status);
        baseMapper.updateById(config);
    }

    @Override
    public String generateConfigId() {
        return "hc_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    @Transactional
    public boolean save(HealthCheckConfig entity) {
        if (StringUtils.isBlank(entity.getConfigId())) {
            entity.setConfigId(generateConfigId());
        }
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }

        boolean success = super.save(entity);

        if (success && entity.getServiceIds() != null && !entity.getServiceIds().isEmpty()) {
            bindServices(entity.getConfigId(), entity.getServiceIds());
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(HealthCheckConfig entity) {
        boolean success = super.updateById(entity);

        if (success && entity.getServiceIds() != null) {
            bindServices(entity.getConfigId(), entity.getServiceIds());
        }

        return success;
    }
}
