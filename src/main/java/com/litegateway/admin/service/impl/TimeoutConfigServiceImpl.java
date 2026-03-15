package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.TimeoutConfig;
import com.litegateway.admin.repository.entity.TimeoutRouteRelation;
import com.litegateway.admin.repository.mapper.TimeoutConfigMapper;
import com.litegateway.admin.repository.mapper.TimeoutRouteRelationMapper;
import com.litegateway.admin.service.TimeoutConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 超时配置服务实现类
 */
@Service
public class TimeoutConfigServiceImpl extends ServiceImpl<TimeoutConfigMapper, TimeoutConfig>
        implements TimeoutConfigService {

    @Autowired
    private TimeoutRouteRelationMapper relationMapper;

    @Override
    public Page<TimeoutConfig> queryPage(String configName, String configType, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<TimeoutConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TimeoutConfig::getDeleted, 0);

        if (StringUtils.isNotBlank(configName)) {
            wrapper.like(TimeoutConfig::getConfigName, configName);
        }
        if (StringUtils.isNotBlank(configType)) {
            wrapper.eq(TimeoutConfig::getConfigType, configType);
        }
        if (status != null) {
            wrapper.eq(TimeoutConfig::getStatus, status);
        }

        wrapper.orderByDesc(TimeoutConfig::getCreateTime);

        Page<TimeoutConfig> page = new Page<>(pageNum, pageSize);
        Page<TimeoutConfig> resultPage = baseMapper.selectPage(page, wrapper);

        // 加载关联的路由ID
        resultPage.getRecords().forEach(config -> {
            if ("route".equals(config.getConfigType())) {
                List<String> routeIds = relationMapper.selectRouteIdsByConfigId(config.getConfigId());
                config.setRouteIds(routeIds);
            }
        });

        return resultPage;
    }

    @Override
    public TimeoutConfig getByConfigId(String configId) {
        TimeoutConfig config = baseMapper.selectByConfigId(configId);
        if (config != null && "route".equals(config.getConfigType())) {
            List<String> routeIds = relationMapper.selectRouteIdsByConfigId(configId);
            config.setRouteIds(routeIds);
        }
        return config;
    }

    @Override
    public List<TimeoutConfig> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<TimeoutConfig> listByConfigType(String configType) {
        return baseMapper.selectByConfigType(configType);
    }

    @Override
    public TimeoutConfig getGlobalConfig() {
        return baseMapper.selectGlobalConfig();
    }

    @Override
    public List<TimeoutConfig> listByRouteId(String routeId) {
        List<String> configIds = relationMapper.selectConfigIdsByRouteId(routeId);
        if (configIds.isEmpty()) {
            return List.of();
        }
        return baseMapper.selectBatchIds(configIds.stream().map(Long::valueOf).collect(Collectors.toList()));
    }

    @Override
    public List<String> getBoundRouteIds(String configId) {
        return relationMapper.selectRouteIdsByConfigId(configId);
    }

    @Override
    @Transactional
    public void bindRoutes(String configId, List<String> routeIds) {
        // 先删除旧关联
        relationMapper.deleteByConfigId(configId);

        // 建立新关联
        if (routeIds != null && !routeIds.isEmpty()) {
            List<TimeoutRouteRelation> relations = routeIds.stream()
                    .map(routeId -> {
                        TimeoutRouteRelation relation = new TimeoutRouteRelation();
                        relation.setConfigId(configId);
                        relation.setRouteId(routeId);
                        relation.setCreateTime(LocalDateTime.now());
                        return relation;
                    })
                    .collect(Collectors.toList());

            relations.forEach(relationMapper::insert);
        }
    }

    @Override
    @Transactional
    public void unbindRoute(String configId, String routeId) {
        LambdaQueryWrapper<TimeoutRouteRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TimeoutRouteRelation::getConfigId, configId)
                .eq(TimeoutRouteRelation::getRouteId, routeId);
        relationMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        TimeoutConfig config = new TimeoutConfig();
        config.setId(id);
        config.setStatus(status);
        baseMapper.updateById(config);
    }

    @Override
    public String generateConfigId() {
        return "timeout_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    @Transactional
    public boolean save(TimeoutConfig entity) {
        // 如果没有设置 configId，自动生成
        if (StringUtils.isBlank(entity.getConfigId())) {
            entity.setConfigId(generateConfigId());
        }

        // 设置默认值
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }

        // 全局配置只能有一个
        if ("global".equals(entity.getConfigType())) {
            TimeoutConfig existingGlobal = baseMapper.selectGlobalConfig();
            if (existingGlobal != null) {
                // 更新现有全局配置
                entity.setId(existingGlobal.getId());
                entity.setConfigId(existingGlobal.getConfigId());
                return updateById(entity);
            }
        }

        boolean success = super.save(entity);

        // 保存关联关系
        if (success && "route".equals(entity.getConfigType()) 
                && entity.getRouteIds() != null && !entity.getRouteIds().isEmpty()) {
            bindRoutes(entity.getConfigId(), entity.getRouteIds());
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(TimeoutConfig entity) {
        boolean success = super.updateById(entity);

        // 更新关联关系
        if (success && "route".equals(entity.getConfigType()) && entity.getRouteIds() != null) {
            bindRoutes(entity.getConfigId(), entity.getRouteIds());
        }

        return success;
    }
}
