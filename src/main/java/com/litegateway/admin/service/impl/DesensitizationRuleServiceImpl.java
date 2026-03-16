package com.litegateway.admin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.litegateway.admin.repository.entity.DesensitizationRule;
import com.litegateway.admin.repository.mapper.DesensitizationRuleMapper;
import com.litegateway.admin.service.DesensitizationRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DesensitizationRuleServiceImpl extends ServiceImpl<DesensitizationRuleMapper, DesensitizationRule>
        implements DesensitizationRuleService {

    @Override
    public DesensitizationRule getByRuleId(String ruleId) {
        return baseMapper.selectByRuleId(ruleId);
    }

    @Override
    public List<DesensitizationRule> getAllEnabled() {
        return baseMapper.selectAllEnabled();
    }

    @Override
    public IPage<DesensitizationRule> queryPage(Page<DesensitizationRule> page, String ruleName,
                                                String dataType, String desensitizationType, Integer enabled) {
        return baseMapper.selectByPage(page, ruleName, dataType, desensitizationType, enabled);
    }

    @Override
    public List<DesensitizationRule> getByRouteId(String routeId) {
        return baseMapper.selectByRouteId(routeId);
    }

    @Override
    public List<DesensitizationRule> getByServiceId(String serviceId) {
        return baseMapper.selectByServiceId(serviceId);
    }

    @Override
    public DesensitizationRule saveRule(DesensitizationRule rule) {
        if (StringUtils.isBlank(rule.getRuleId())) {
            rule.setRuleId("desen_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        }
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        if (rule.getPriority() == null) {
            rule.setPriority(100);
        }
        if (rule.getEnabled() == null) {
            rule.setEnabled(1);
        }
        if (rule.getApplyRequest() == null) {
            rule.setApplyRequest(0);
        }
        if (rule.getApplyResponse() == null) {
            rule.setApplyResponse(1);
        }
        save(rule);
        return rule;
    }

    @Override
    public boolean updateRule(DesensitizationRule rule) {
        DesensitizationRule existing = getByRuleId(rule.getRuleId());
        if (existing == null) {
            return false;
        }
        rule.setId(existing.getId());
        rule.setUpdateTime(LocalDateTime.now());
        return updateById(rule);
    }

    @Override
    public boolean deleteRule(String ruleId) {
        DesensitizationRule existing = getByRuleId(ruleId);
        if (existing == null) {
            return false;
        }
        return removeById(existing.getId());
    }

    @Override
    public boolean updateStatus(String ruleId, Integer enabled) {
        DesensitizationRule existing = getByRuleId(ruleId);
        if (existing == null) {
            return false;
        }
        existing.setEnabled(enabled);
        existing.setUpdateTime(LocalDateTime.now());
        return updateById(existing);
    }
}
