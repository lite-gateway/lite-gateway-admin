package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.event.CircuitBreakerRuleChangeEvent;
import com.litegateway.admin.repository.entity.CircuitBreakerRule;
import com.litegateway.admin.repository.mapper.CircuitBreakerRuleMapper;
import com.litegateway.admin.service.CircuitBreakerRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 熔断规则服务实现类
 */
@Service
public class CircuitBreakerRuleServiceImpl extends ServiceImpl<CircuitBreakerRuleMapper, CircuitBreakerRule>
        implements CircuitBreakerRuleService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Page<CircuitBreakerRule> queryPage(String ruleName, String routeId, Boolean enabled, int pageNum, int pageSize) {
        LambdaQueryWrapper<CircuitBreakerRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircuitBreakerRule::getDeleted, 0);

        if (StringUtils.isNotBlank(ruleName)) {
            wrapper.like(CircuitBreakerRule::getRuleName, ruleName);
        }
        if (StringUtils.isNotBlank(routeId)) {
            wrapper.eq(CircuitBreakerRule::getRouteId, routeId);
        }
        if (enabled != null) {
            wrapper.eq(CircuitBreakerRule::getEnabled, enabled);
        }

        wrapper.orderByDesc(CircuitBreakerRule::getCreateTime);

        Page<CircuitBreakerRule> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public CircuitBreakerRule getByRuleId(String ruleId) {
        return baseMapper.selectByRuleId(ruleId);
    }

    @Override
    public List<CircuitBreakerRule> listAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public List<CircuitBreakerRule> listByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public CircuitBreakerRule getGlobalRule() {
        return baseMapper.selectGlobalRule();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Boolean enabled) {
        CircuitBreakerRule rule = new CircuitBreakerRule();
        rule.setId(id);
        rule.setEnabled(enabled);
        baseMapper.updateById(rule);

        // 发布变更事件
        CircuitBreakerRule existing = baseMapper.selectById(id);
        if (existing != null) {
            eventPublisher.publishEvent(new CircuitBreakerRuleChangeEvent(this, existing.getRuleId()));
        }
    }

    @Override
    public void publishRuleChangeEvent(String ruleId) {
        eventPublisher.publishEvent(new CircuitBreakerRuleChangeEvent(this, ruleId));
    }

    @Override
    @Transactional
    public boolean save(CircuitBreakerRule entity) {
        // 如果没有设置 ruleId，自动生成
        if (StringUtils.isBlank(entity.getRuleId())) {
            entity.setRuleId("cb_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }

        // 设置默认值
        setDefaultValues(entity);

        boolean success = super.save(entity);

        // 发布变更事件
        if (success) {
            eventPublisher.publishEvent(new CircuitBreakerRuleChangeEvent(this, entity.getRuleId()));
        }

        return success;
    }

    @Override
    @Transactional
    public boolean updateById(CircuitBreakerRule entity) {
        // 获取旧数据用于比较
        CircuitBreakerRule oldRule = null;
        if (entity.getId() != null) {
            oldRule = baseMapper.selectById(entity.getId());
        }

        // 设置默认值（仅对新字段）
        setDefaultValues(entity);

        boolean success = super.updateById(entity);

        // 发布变更事件
        if (success) {
            String ruleId = entity.getRuleId();
            if (StringUtils.isBlank(ruleId) && oldRule != null) {
                ruleId = oldRule.getRuleId();
            }

            boolean paramsChanged = oldRule == null
                    || !Objects.equals(oldRule.getEnabled(), entity.getEnabled())
                    || !Objects.equals(oldRule.getFailureRateThreshold(), entity.getFailureRateThreshold())
                    || !Objects.equals(oldRule.getWaitDurationInOpenState(), entity.getWaitDurationInOpenState())
                    || !Objects.equals(oldRule.getSlidingWindowSize(), entity.getSlidingWindowSize())
                    || !Objects.equals(oldRule.getTimeoutDuration(), entity.getTimeoutDuration());

            if (paramsChanged && StringUtils.isNotBlank(ruleId)) {
                eventPublisher.publishEvent(new CircuitBreakerRuleChangeEvent(this, ruleId));
            }
        }

        return success;
    }

    @Override
    @Transactional
    public boolean removeById(CircuitBreakerRule entity) {
        CircuitBreakerRule existing = baseMapper.selectById(entity.getId());
        if (existing != null) {
            eventPublisher.publishEvent(new CircuitBreakerRuleChangeEvent(this, existing.getRuleId()));
        }
        return super.removeById(entity);
    }

    private void setDefaultValues(CircuitBreakerRule entity) {
        if (entity.getFailureRateThreshold() == null) {
            entity.setFailureRateThreshold(50f);
        }
        if (entity.getWaitDurationInOpenState() == null) {
            entity.setWaitDurationInOpenState(60);
        }
        if (entity.getPermittedNumberOfCallsInHalfOpenState() == null) {
            entity.setPermittedNumberOfCallsInHalfOpenState(10);
        }
        if (entity.getSlidingWindowSize() == null) {
            entity.setSlidingWindowSize(100);
        }
        if (entity.getMinimumNumberOfCalls() == null) {
            entity.setMinimumNumberOfCalls(10);
        }
        if (entity.getSlowCallRateThreshold() == null) {
            entity.setSlowCallRateThreshold(50f);
        }
        if (entity.getSlowCallDurationThreshold() == null) {
            entity.setSlowCallDurationThreshold(5);
        }
        if (entity.getTimeoutDuration() == null) {
            entity.setTimeoutDuration(5);
        }
        if (entity.getEnabled() == null) {
            entity.setEnabled(true);
        }
    }
}
