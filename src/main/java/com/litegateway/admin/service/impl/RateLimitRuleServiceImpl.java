package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.event.RateLimitChangeEvent;
import com.litegateway.admin.event.RateLimitRouteRelationChangeEvent;
import com.litegateway.admin.repository.entity.RateLimitRouteRelation;
import com.litegateway.admin.repository.entity.RateLimitRule;
import com.litegateway.admin.repository.mapper.RateLimitRouteRelationMapper;
import com.litegateway.admin.repository.mapper.RateLimitRuleMapper;
import com.litegateway.admin.service.RateLimitRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 限流规则服务实现类
 */
@Service
public class RateLimitRuleServiceImpl extends ServiceImpl<RateLimitRuleMapper, RateLimitRule>
        implements RateLimitRuleService {

    @Autowired
    private RateLimitRouteRelationMapper relationMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<RateLimitRule> queryPage(String ruleName, Integer limitType, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<RateLimitRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RateLimitRule::getDeleted, 0);
        
        if (StringUtils.isNotBlank(ruleName)) {
            wrapper.like(RateLimitRule::getRuleName, ruleName);
        }
        if (limitType != null) {
            wrapper.eq(RateLimitRule::getLimitType, limitType);
        }
        if (status != null) {
            wrapper.eq(RateLimitRule::getStatus, status);
        }
        
        wrapper.orderByDesc(RateLimitRule::getCreateTime);
        
        Page<RateLimitRule> page = new Page<>(pageNum, pageSize);
        Page<RateLimitRule> resultPage = baseMapper.selectPage(page, wrapper);
        
        // 加载关联的路由ID
        resultPage.getRecords().forEach(rule -> {
            List<String> routeIds = relationMapper.selectRouteIdsByRuleId(rule.getRuleId());
            rule.setRouteIds(routeIds);
        });
        
        return resultPage;
    }

    @Override
    public RateLimitRule getByRuleId(String ruleId) {
        return baseMapper.selectByRuleId(ruleId);
    }

    @Override
    public List<RateLimitRule> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<RateLimitRule> listByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public List<String> getBoundRouteIds(String ruleId) {
        return relationMapper.selectRouteIdsByRuleId(ruleId);
    }

    @Override
    @Transactional
    public void bindRoutes(String ruleId, List<String> routeIds) {
        // 获取已绑定的路由ID
        List<String> oldRouteIds = relationMapper.selectRouteIdsByRuleId(ruleId);
        
        // 先删除旧关联
        relationMapper.deleteByRuleId(ruleId);
        
        // 建立新关联
        if (routeIds != null && !routeIds.isEmpty()) {
            List<RateLimitRouteRelation> relations = routeIds.stream()
                    .map(routeId -> {
                        RateLimitRouteRelation relation = new RateLimitRouteRelation();
                        relation.setRateLimitRuleId(ruleId);
                        relation.setRouteId(routeId);
                        return relation;
                    })
                    .collect(Collectors.toList());
            
            relations.forEach(relationMapper::insert);
        }
        
        // 发布关联变更事件
        // 对于新绑定的路由
        if (routeIds != null) {
            for (String routeId : routeIds) {
                if (!oldRouteIds.contains(routeId)) {
                    eventPublisher.publishEvent(new RateLimitRouteRelationChangeEvent(
                            this, routeId, ruleId, RateLimitRouteRelationChangeEvent.ChangeType.BIND));
                }
            }
        }
        // 对于解绑的路由
        for (String oldRouteId : oldRouteIds) {
            if (routeIds == null || !routeIds.contains(oldRouteId)) {
                eventPublisher.publishEvent(new RateLimitRouteRelationChangeEvent(
                        this, oldRouteId, ruleId, RateLimitRouteRelationChangeEvent.ChangeType.UNBIND));
            }
        }
    }

    @Override
    @Transactional
    public void unbindRoute(String ruleId, String routeId) {
        relationMapper.deleteByRuleIdAndRouteId(ruleId, routeId);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        RateLimitRule rule = new RateLimitRule();
        rule.setId(id);
        rule.setStatus(status);
        baseMapper.updateById(rule);
    }

    @Override
    @Transactional
    public boolean save(RateLimitRule entity) {
        // 如果没有设置 ruleId，自动生成
        if (StringUtils.isBlank(entity.getRuleId())) {
            entity.setRuleId("rule_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        
        // 设置默认值
        if (StringUtils.isBlank(entity.getAlgorithm())) {
            entity.setAlgorithm("token-bucket");
        }
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        
        boolean success = super.save(entity);
        
        // 保存关联关系
        if (success && entity.getRouteIds() != null && !entity.getRouteIds().isEmpty()) {
            bindRoutes(entity.getRuleId(), entity.getRouteIds());
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean updateById(RateLimitRule entity) {
        // 获取旧数据用于比较
        RateLimitRule oldRule = null;
        if (entity.getId() != null) {
            oldRule = baseMapper.selectById(entity.getId());
        }
        
        boolean success = super.updateById(entity);
        
        // 更新关联关系
        if (success && entity.getRouteIds() != null) {
            bindRoutes(entity.getRuleId(), entity.getRouteIds());
        }
        
        // 发布限流规则变更事件（当限流参数变更时）
        if (success && oldRule != null) {
            boolean rateLimitParamsChanged = !Objects.equals(oldRule.getReplenishRate(), entity.getReplenishRate())
                    || !Objects.equals(oldRule.getBurstCapacity(), entity.getBurstCapacity())
                    || !Objects.equals(oldRule.getRequestedTokens(), entity.getRequestedTokens())
                    || !Objects.equals(oldRule.getAlgorithm(), entity.getAlgorithm())
                    || !Objects.equals(oldRule.getStatus(), entity.getStatus());
            
            if (rateLimitParamsChanged) {
                eventPublisher.publishEvent(new RateLimitChangeEvent(this, entity.getRuleId()));
            }
        }
        
        return success;
    }
}
