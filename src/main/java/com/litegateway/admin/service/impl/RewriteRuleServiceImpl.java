package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.RewriteRule;
import com.litegateway.admin.repository.mapper.RewriteRuleMapper;
import com.litegateway.admin.service.RewriteRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RewriteRuleServiceImpl extends ServiceImpl<RewriteRuleMapper, RewriteRule>
        implements RewriteRuleService {

    @Override
    public RewriteRule getByRuleId(String ruleId) {
        return baseMapper.selectByRuleId(ruleId);
    }

    @Override
    public List<RewriteRule> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public IPage<RewriteRule> queryPage(Page<RewriteRule> page, String ruleName,
                                        String matchType, String rewriteType, Integer enabled) {
        return baseMapper.selectByPage(page, ruleName, matchType, rewriteType, enabled);
    }

    @Override
    public List<RewriteRule> getByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public List<RewriteRule> getByServiceId(String serviceId) {
        return baseMapper.selectByServiceId(serviceId);
    }

    @Override
    public RewriteRule saveRule(RewriteRule rule) {
        if (StringUtils.isBlank(rule.getRuleId())) {
            rule.setRuleId("rewrite_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        if (rule.getPriority() == null) {
            rule.setPriority(100);
        }
        if (rule.getEnabled() == null) {
            rule.setEnabled(1);
        }
        save(rule);
        return rule;
    }

    @Override
    public boolean updateRule(RewriteRule rule) {
        RewriteRule existing = getByRuleId(rule.getRuleId());
        if (existing == null) {
            return false;
        }
        rule.setId(existing.getId());
        rule.setUpdateTime(LocalDateTime.now());
        return updateById(rule);
    }

    @Override
    public boolean deleteRule(String ruleId) {
        RewriteRule existing = getByRuleId(ruleId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public boolean updateStatus(String ruleId, Integer enabled) {
        RewriteRule existing = getByRuleId(ruleId);
        if (existing == null) {
            return false;
        }
        existing.setEnabled(enabled);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }
}
