package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.AuthConfig;
import com.litegateway.admin.repository.entity.AuthRouteRelation;
import com.litegateway.admin.repository.mapper.AuthConfigMapper;
import com.litegateway.admin.repository.mapper.AuthRouteRelationMapper;
import com.litegateway.admin.service.AuthConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 鉴权配置服务实现类
 */
@Service
public class AuthConfigServiceImpl extends ServiceImpl<AuthConfigMapper, AuthConfig>
        implements AuthConfigService {

    @Autowired
    private AuthRouteRelationMapper relationMapper;

    @Override
    public Page<AuthConfig> queryPage(String configName, String authType, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<AuthConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthConfig::getDeleted, 0);

        if (StringUtils.isNotBlank(configName)) {
            wrapper.like(AuthConfig::getConfigName, configName);
        }
        if (StringUtils.isNotBlank(authType)) {
            wrapper.eq(AuthConfig::getAuthType, authType);
        }
        if (status != null) {
            wrapper.eq(AuthConfig::getStatus, status);
        }

        wrapper.orderByDesc(AuthConfig::getCreateTime);

        Page<AuthConfig> page = new Page<>(pageNum, pageSize);
        Page<AuthConfig> resultPage = baseMapper.selectPage(page, wrapper);

        // 加载关联的路由ID
        resultPage.getRecords().forEach(config -> {
            List<String> routeIds = relationMapper.selectRouteIdsByConfigId(config.getConfigId());
            config.setRouteIds(routeIds);
        });

        return resultPage;
    }

    @Override
    public AuthConfig getByConfigId(String configId) {
        AuthConfig config = baseMapper.selectByConfigId(configId);
        if (config != null) {
            List<String> routeIds = relationMapper.selectRouteIdsByConfigId(configId);
            config.setRouteIds(routeIds);
        }
        return config;
    }

    @Override
    public List<AuthConfig> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<AuthConfig> listByAuthType(String authType) {
        return baseMapper.selectByAuthType(authType);
    }

    @Override
    public List<AuthConfig> listByRouteId(String routeId) {
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
            List<AuthRouteRelation> relations = routeIds.stream()
                    .map(routeId -> {
                        AuthRouteRelation relation = new AuthRouteRelation();
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
        LambdaQueryWrapper<AuthRouteRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthRouteRelation::getConfigId, configId)
                .eq(AuthRouteRelation::getRouteId, routeId);
        relationMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        AuthConfig config = new AuthConfig();
        config.setId(id);
        config.setStatus(status);
        baseMapper.updateById(config);
    }

    @Override
    public String generateConfigId() {
        return "auth_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    @Override
    @Transactional
    public boolean save(AuthConfig entity) {
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
        if (success && entity.getRouteIds() != null && !entity.getRouteIds().isEmpty()) {
            bindRoutes(entity.getConfigId(), entity.getRouteIds());
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(AuthConfig entity) {
        boolean success = super.updateById(entity);

        // 更新关联关系
        if (success && entity.getRouteIds() != null) {
            bindRoutes(entity.getConfigId(), entity.getRouteIds());
        }

        return success;
    }
}
