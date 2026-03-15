package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.LoadBalanceConfig;
import com.litegateway.admin.repository.entity.LoadBalanceServiceRelation;
import com.litegateway.admin.repository.mapper.LoadBalanceConfigMapper;
import com.litegateway.admin.repository.mapper.LoadBalanceServiceRelationMapper;
import com.litegateway.admin.service.LoadBalanceConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 负载均衡配置服务实现类
 */
@Service
public class LoadBalanceConfigServiceImpl extends ServiceImpl<LoadBalanceConfigMapper, LoadBalanceConfig>
        implements LoadBalanceConfigService {

    @Autowired
    private LoadBalanceServiceRelationMapper relationMapper;

    @Override
    public Page<LoadBalanceConfig> queryPage(String configName, String strategy, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<LoadBalanceConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoadBalanceConfig::getDeleted, 0);

        if (StringUtils.isNotBlank(configName)) {
            wrapper.like(LoadBalanceConfig::getConfigName, configName);
        }
        if (StringUtils.isNotBlank(strategy)) {
            wrapper.eq(LoadBalanceConfig::getStrategy, strategy);
        }
        if (status != null) {
            wrapper.eq(LoadBalanceConfig::getStatus, status);
        }

        wrapper.orderByDesc(LoadBalanceConfig::getCreateTime);

        Page<LoadBalanceConfig> page = new Page<>(pageNum, pageSize);
        Page<LoadBalanceConfig> resultPage = baseMapper.selectPage(page, wrapper);

        // 加载关联的服务ID
        resultPage.getRecords().forEach(config -> {
            List<String> serviceIds = relationMapper.selectServiceIdsByConfigId(config.getConfigId());
            config.setServiceIds(serviceIds);
        });

        return resultPage;
    }

    @Override
    public LoadBalanceConfig getByConfigId(String configId) {
        LoadBalanceConfig config = baseMapper.selectByConfigId(configId);
        if (config != null) {
            List<String> serviceIds = relationMapper.selectServiceIdsByConfigId(configId);
            config.setServiceIds(serviceIds);
        }
        return config;
    }

    @Override
    public List<LoadBalanceConfig> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<LoadBalanceConfig> listByStrategy(String strategy) {
        return baseMapper.selectByStrategy(strategy);
    }

    @Override
    public List<LoadBalanceConfig> listByServiceId(String serviceId) {
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
        // 先删除旧关联
        relationMapper.deleteByConfigId(configId);

        // 建立新关联
        if (serviceIds != null && !serviceIds.isEmpty()) {
            List<LoadBalanceServiceRelation> relations = serviceIds.stream()
                    .map(serviceId -> {
                        LoadBalanceServiceRelation relation = new LoadBalanceServiceRelation();
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
    public void unbindService(String configId, String serviceId) {
        LambdaQueryWrapper<LoadBalanceServiceRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoadBalanceServiceRelation::getConfigId, configId)
                .eq(LoadBalanceServiceRelation::getServiceId, serviceId);
        relationMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        LoadBalanceConfig config = new LoadBalanceConfig();
        config.setId(id);
        config.setStatus(status);
        baseMapper.updateById(config);
    }

    @Override
    public String generateConfigId() {
        return "lb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    @Transactional
    public boolean save(LoadBalanceConfig entity) {
        // 如果没有设置 configId，自动生成
        if (StringUtils.isBlank(entity.getConfigId())) {
            entity.setConfigId(generateConfigId());
        }

        // 设置默认值
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }

        boolean success = super.save(entity);

        // 保存关联关系
        if (success && entity.getServiceIds() != null && !entity.getServiceIds().isEmpty()) {
            bindServices(entity.getConfigId(), entity.getServiceIds());
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(LoadBalanceConfig entity) {
        boolean success = super.updateById(entity);

        // 更新关联关系
        if (success && entity.getServiceIds() != null) {
            bindServices(entity.getConfigId(), entity.getServiceIds());
        }

        return success;
    }
}
