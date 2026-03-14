package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.event.CanaryRuleChangeEvent;
import com.litegateway.admin.repository.entity.CanaryRule;
import com.litegateway.admin.repository.mapper.CanaryRuleMapper;
import com.litegateway.admin.service.CanaryRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 灰度规则服务实现类
 */
@Service
public class CanaryRuleServiceImpl extends ServiceImpl<CanaryRuleMapper, CanaryRule>
        implements CanaryRuleService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<CanaryRule> queryPage(String ruleName, String routeId, Boolean enabled, int pageNum, int pageSize) {
        LambdaQueryWrapper<CanaryRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CanaryRule::getDeleted, 0);

        if (StringUtils.isNotBlank(ruleName)) {
            wrapper.like(CanaryRule::getRuleName, ruleName);
        }
        if (StringUtils.isNotBlank(routeId)) {
            wrapper.eq(CanaryRule::getRouteId, routeId);
        }
        if (enabled != null) {
            wrapper.eq(CanaryRule::getEnabled, enabled);
        }

        wrapper.orderByDesc(CanaryRule::getCreateTime);

        Page<CanaryRule> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public CanaryRule getByRuleId(String ruleId) {
        return baseMapper.selectByRuleId(ruleId);
    }

    @Override
    public List<CanaryRule> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<CanaryRule> listByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Boolean enabled) {
        CanaryRule rule = new CanaryRule();
        rule.setId(id);
        rule.setEnabled(enabled);
        baseMapper.updateById(rule);

        // 发布变更事件
        CanaryRule existing = baseMapper.selectById(id);
        if (existing != null) {
            eventPublisher.publishEvent(new CanaryRuleChangeEvent(this, existing.getRuleId(), existing.getRouteId()));
        }
    }

    @Override
    public void publishRuleChangeEvent(String ruleId) {
        CanaryRule rule = getByRuleId(ruleId);
        if (rule != null) {
            eventPublisher.publishEvent(new CanaryRuleChangeEvent(this, ruleId, rule.getRouteId()));
        }
    }

    @Override
    @Transactional
    public boolean save(CanaryRule entity) {
        // 如果没有设置 ruleId，自动生成
        if (StringUtils.isBlank(entity.getRuleId())) {
            entity.setRuleId("canary_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }

        // 设置默认值
        setDefaultValues(entity);

        boolean success = super.save(entity);

        // 发布变更事件
        if (success) {
            eventPublisher.publishEvent(new CanaryRuleChangeEvent(this, entity.getRuleId(), entity.getRouteId()));
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(CanaryRule entity) {
        // 获取旧数据用于比较
        CanaryRule oldRule = null;
        if (entity.getId() != null) {
            oldRule = baseMapper.selectById(entity.getId());
        }

        // 设置默认值（仅对新字段）
        setDefaultValues(entity);

        boolean success = super.updateById(entity);

        // 发布变更事件
        if (success) {
            String ruleId = entity.getRuleId();
            String routeId = entity.getRouteId();
            if (StringUtils.isBlank(ruleId) && oldRule != null) {
                ruleId = oldRule.getRuleId();
            }
            if (StringUtils.isBlank(routeId) && oldRule != null) {
                routeId = oldRule.getRouteId();
            }

            boolean paramsChanged = oldRule == null
                    || !Objects.equals(oldRule.getEnabled(), entity.getEnabled())
                    || !Objects.equals(oldRule.getCanaryWeight(), entity.getCanaryWeight())
                    || !Objects.equals(oldRule.getCanaryVersion(), entity.getCanaryVersion())
                    || !Objects.equals(oldRule.getStableVersion(), entity.getStableVersion())
                    || !Objects.equals(oldRule.getMatchType(), entity.getMatchType())
                    || !Objects.equals(oldRule.getMatchConfig(), entity.getMatchConfig());

            if (paramsChanged && StringUtils.isNotBlank(ruleId)) {
                eventPublisher.publishEvent(new CanaryRuleChangeEvent(this, ruleId, routeId));
            }
        }

        return success;
    }

    @Override
    @Transactional
    public boolean removeById(CanaryRule entity) {
        CanaryRule existing = baseMapper.selectById(entity.getId());
        if (existing != null) {
            eventPublisher.publishEvent(new CanaryRuleChangeEvent(this, existing.getRuleId(), existing.getRouteId()));
        }
        return super.removeById(entity);
    }

    private void setDefaultValues(CanaryRule entity) {
        if (entity.getCanaryWeight() == null) {
            entity.setCanaryWeight(10);
        }
        if (entity.getCanaryVersion() == null) {
            entity.setCanaryVersion("v2");
        }
        if (entity.getStableVersion() == null) {
            entity.setStableVersion("v1");
        }
        if (StringUtils.isBlank(entity.getMatchType())) {
            entity.setMatchType("weight");
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
    }
}
