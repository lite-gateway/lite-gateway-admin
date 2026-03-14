package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.event.FeatureConfigChangeEvent;
import com.litegateway.admin.repository.entity.GatewayFeatureConfig;
import com.litegateway.admin.repository.mapper.GatewayFeatureConfigMapper;
import com.litegateway.admin.service.GatewayFeatureConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 功能配置服务实现类
 */
@Service
public class GatewayFeatureConfigServiceImpl extends ServiceImpl<GatewayFeatureConfigMapper, GatewayFeatureConfig>
        implements GatewayFeatureConfigService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<GatewayFeatureConfig> queryPage(String featureName, Boolean enabled, int pageNum, int pageSize) {
        LambdaQueryWrapper<GatewayFeatureConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GatewayFeatureConfig::getDeleted, 0);

        if (StringUtils.isNotBlank(featureName)) {
            wrapper.like(GatewayFeatureConfig::getFeatureName, featureName)
                    .or()
                    .like(GatewayFeatureConfig::getFeatureCode, featureName);
        }
        if (enabled != null) {
            wrapper.eq(GatewayFeatureConfig::getEnabled, enabled);
        }

        wrapper.orderByAsc(GatewayFeatureConfig::getPriority);

        Page<GatewayFeatureConfig> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public GatewayFeatureConfig getByFeatureCode(String featureCode) {
        return baseMapper.selectByFeatureCode(featureCode);
    }

    @Override
    public List<GatewayFeatureConfig> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<GatewayFeatureConfig> listByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Boolean enabled) {
        GatewayFeatureConfig config = new GatewayFeatureConfig();
        config.setId(id);
        config.setEnabled(enabled);
        baseMapper.updateById(config);

        // 发布变更事件
        GatewayFeatureConfig existing = baseMapper.selectById(id);
        if (existing != null) {
            eventPublisher.publishEvent(new FeatureConfigChangeEvent(this, existing.getFeatureCode()));
        }
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Boolean enabled) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            updateStatus(id, enabled);
        }
    }

    @Override
    @Transactional
    public boolean save(GatewayFeatureConfig entity) {
        // 设置默认值
        if (entity.getPriority() == null) {
            entity.setPriority(100);
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }

        boolean success = super.save(entity);

        // 发布变更事件
        if (success) {
            eventPublisher.publishEvent(new FeatureConfigChangeEvent(this, entity.getFeatureCode()));
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(GatewayFeatureConfig entity) {
        // 获取旧数据用于比较
        GatewayFeatureConfig oldConfig = null;
        if (entity.getId() != null) {
            oldConfig = baseMapper.selectById(entity.getId());
        }

        boolean success = super.updateById(entity);

        // 发布变更事件
        if (success) {
            String featureCode = entity.getFeatureCode();
            if (StringUtils.isBlank(featureCode) && oldConfig != null) {
                featureCode = oldConfig.getFeatureCode();
            }

            boolean configChanged = oldConfig == null
                    || !Objects.equals(oldConfig.getEnabled(), entity.getEnabled())
                    || !Objects.equals(oldConfig.getConfigJson(), entity.getConfigJson())
                    || !Objects.equals(oldConfig.getRoutePatterns(), entity.getRoutePatterns())
                    || !Objects.equals(oldConfig.getPriority(), entity.getPriority());

            if (configChanged && StringUtils.isNotBlank(featureCode)) {
                eventPublisher.publishEvent(new FeatureConfigChangeEvent(this, featureCode));
            }
        }

        return success;
    }

    @Override
    @Transactional
    public boolean removeById(GatewayFeatureConfig entity) {
        GatewayFeatureConfig existing = baseMapper.selectById(entity.getId());
        if (existing != null) {
            eventPublisher.publishEvent(new FeatureConfigChangeEvent(this, existing.getFeatureCode()));
        }
        return super.removeById(entity);
    }
}
