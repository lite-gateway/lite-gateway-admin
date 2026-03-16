package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.TraceConfig;
import com.litegateway.admin.repository.entity.TraceRouteRelation;
import com.litegateway.admin.repository.mapper.TraceConfigMapper;
import com.litegateway.admin.repository.mapper.TraceRouteRelationMapper;
import com.litegateway.admin.service.TraceConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TraceConfigServiceImpl extends ServiceImpl<TraceConfigMapper, TraceConfig>
        implements TraceConfigService {

    @Autowired
    private TraceRouteRelationMapper relationMapper;

    @Override
    public Page<TraceConfig> queryPage(String configName, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<TraceConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TraceConfig::getDeleted, 0);

        if (StringUtils.isNotBlank(configName)) {
            wrapper.like(TraceConfig::getConfigName, configName);
        }
        if (status != null) {
            wrapper.eq(TraceConfig::getStatus, status);
        }

        wrapper.orderByDesc(TraceConfig::getCreateTime);

        Page<TraceConfig> page = new Page<>(pageNum, pageSize);
        Page<TraceConfig> resultPage = baseMapper.selectPage(page, wrapper);

        resultPage.getRecords().forEach(config -> {
            List<String> routeIds = relationMapper.selectRouteIdsByConfigId(config.getConfigId());
            config.setRouteIds(routeIds);
        });

        return resultPage;
    }

    @Override
    public TraceConfig getByConfigId(String configId) {
        TraceConfig config = baseMapper.selectByConfigId(configId);
        if (config != null) {
            List<String> routeIds = relationMapper.selectRouteIdsByConfigId(configId);
            config.setRouteIds(routeIds);
        }
        return config;
    }

    @Override
    public List<TraceConfig> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<String> getBoundRouteIds(String configId) {
        return relationMapper.selectRouteIdsByConfigId(configId);
    }

    @Override
    @Transactional
    public void bindRoutes(String configId, List<String> routeIds) {
        relationMapper.deleteByConfigId(configId);

        if (routeIds != null && !routeIds.isEmpty()) {
            List<TraceRouteRelation> relations = routeIds.stream()
                    .map(routeId -> {
                        TraceRouteRelation relation = new TraceRouteRelation();
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
    public void updateStatus(Long id, Integer status) {
        TraceConfig config = new TraceConfig();
        config.setId(id);
        config.setStatus(status);
        baseMapper.updateById(config);
    }

    @Override
    public String generateConfigId() {
        return "trace_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    @Transactional
    public boolean save(TraceConfig entity) {
        if (StringUtils.isBlank(entity.getConfigId())) {
            entity.setConfigId(generateConfigId());
        }
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }

        boolean success = super.save(entity);

        if (success && entity.getRouteIds() != null && !entity.getRouteIds().isEmpty()) {
            bindRoutes(entity.getConfigId(), entity.getRouteIds());
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(TraceConfig entity) {
        boolean success = super.updateById(entity);

        if (success && entity.getRouteIds() != null) {
            bindRoutes(entity.getConfigId(), entity.getRouteIds());
        }

        return success;
    }
}
